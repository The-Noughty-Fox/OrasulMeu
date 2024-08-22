package com.thenoughtfox.orasulmeu.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.InvalidatingPagingSourceFactory
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.mapbox.geojson.Point
import com.thenoughtfox.orasulmeu.net.helper.toOperationResult
import com.thenoughtfox.orasulmeu.service.UserSharedPrefs
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeContract.Action
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeContract.Event
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeContract.PostListEvents
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeContract.PostListSorting
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeContract.State
import com.thenoughtfox.orasulmeu.ui.screens.home.utils.CombinedPostsPagingSource
import com.thenoughtfox.orasulmeu.ui.screens.home.utils.PostType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.openapitools.client.apis.PostsApi
import org.openapitools.client.models.PostDto
import org.openapitools.client.models.ReactToPostDto
import org.openapitools.client.models.ReactToPostDto.Reaction
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: PostsApi,
    private val userSharedPrefs: UserSharedPrefs
) : ViewModel() {

    private val _state: MutableStateFlow<State> = MutableStateFlow(State(isLoading = true))
    val state = _state.asStateFlow()

    private val _event: Channel<Event> = Channel(Channel.UNLIMITED)

    private val _action = MutableSharedFlow<Action>()
    val action: SharedFlow<Action> = _action

    private val modificationEvents = MutableStateFlow<List<PostListEvents>>(emptyList())

    private val newPostsInvalidatingSourceFactory = InvalidatingPagingSourceFactory {
        CombinedPostsPagingSource(api).getPostsPagingSource(type = PostType.NEW)
    }

    val newPostsPager = Pager(
        PagingConfig(pageSize = 20),
        pagingSourceFactory = newPostsInvalidatingSourceFactory
    ).flow
        .cachedIn(viewModelScope)
        .combine(modificationEvents) { pagingData, modifications ->
            modifications.fold(pagingData) { acc, event ->
                applyPostListEvents(acc, event)
            }
        }

    private val popularPostsInvalidatingSourceFactory = InvalidatingPagingSourceFactory {
        CombinedPostsPagingSource(api).getPostsPagingSource(type = PostType.POPULAR)
    }

    val popularPostsPager = Pager(
        PagingConfig(pageSize = 20),
        pagingSourceFactory = popularPostsInvalidatingSourceFactory
    ).flow
        .cachedIn(viewModelScope)
        .combine(modificationEvents) { pagingData, modifications ->
            modifications.fold(pagingData) { acc, event ->
                applyPostListEvents(acc, event)
            }
        }

    private val searchInvalidatingSourceFactory = InvalidatingPagingSourceFactory {
        CombinedPostsPagingSource(api).getPostsPagingSource(
            type = PostType.SEARCH, phrase = state.value.searchText
        )
    }

    val searchPostsPager = Pager(
        PagingConfig(pageSize = 20),
        pagingSourceFactory = searchInvalidatingSourceFactory
    ).flow
        .cachedIn(viewModelScope)
        .combine(modificationEvents) { pagingData, modifications ->
            modifications.fold(pagingData) { acc, event ->
                applyPostListEvents(acc, event)
            }
        }

    suspend fun sendEvent(a: Event) {
        _event.send(a)
    }

    init {
        handleEvents()
        getAllPopularPosts()
        setInitialLocation()
    }

    private fun handleEvents() = viewModelScope.launch {
        _event.consumeAsFlow().collect { event ->
            when (event) {
                is Event.DislikePost -> {
                    reactToPost(event.postId, Reaction.dislike)
                }

                is Event.LikePost -> {
                    reactToPost(event.postId, Reaction.like)
                }

                is Event.RevokeReaction -> {
                    revokeReaction(event.postId)
                }

                is Event.SendReport -> {
                    _state.update { it.copy(messageToShow = "Feature will be implemented soon") }
                }

                Event.CloseMessage -> {
                    _state.update { it.copy(messageToShow = null) }
                }

                is Event.SelectListSorting -> {
                    applyNewSorting(event.sortType)
                }

                is Event.SearchPostWithText -> {
                    searchPosts(event.searchText)
                }

                is Event.NavigateToUser -> {
                    userSharedPrefs.user = userSharedPrefs.user?.copy(
                        latitude = event.point.latitude(),
                        longitude = event.point.longitude()
                    )

                    _state.update { it.copy(lastLocation = event.point) }
                    _action.emit(Action.MoveToLocation(event.point))
                }

                Event.Refresh -> {
                    _state.update { it.copy(isRefreshing = true) }
                    if (state.value.postListSorting == PostListSorting.New) {
                        newPostsInvalidatingSourceFactory.invalidate()
                    } else {
                        popularPostsInvalidatingSourceFactory.invalidate()
                    }

                    _state.update { it.copy(isRefreshing = false) }
                }

                Event.RefreshNewPosts -> {
                    _state.update { it.copy(postListSorting = PostListSorting.New) }
                    newPostsInvalidatingSourceFactory.invalidate()
                }
            }
        }
    }

    private fun setInitialLocation() {
        val lat = userSharedPrefs.user?.latitude ?: return
        val lon = userSharedPrefs.user?.longitude ?: return

        _state.update {
            it.copy(
                lastLocation = Point.fromLngLat(lon, lat),
                isLoading = true
            )
        }
    }

    private fun getAllPopularPosts() = viewModelScope.launch(Dispatchers.IO) {
        api.getAllPostsOrderedByReactionsCount(limit = 100)
            .toOperationResult { it }
            .onSuccess { response ->
                val reactedPosts = response.data ?: emptyList()
                _state.update {
                    it.copy(isLoading = false, popularPosts = reactedPosts)
                }
            }
    }

    private fun applyPostListEvents(
        paging: PagingData<PostDto>, events: PostListEvents
    ): PagingData<PostDto> {
        return when (events) {
            is PostListEvents.Reaction -> {
                paging.map {
                    if (it.id == events.postId) {
                        it.copy(reactions = events.reactionsDto)
                    } else {
                        it
                    }
                }
            }

            else -> paging
        }
    }

    private fun applyNewSorting(newValue: PostListSorting) {
        _state.update { it.copy(postListSorting = newValue) }
    }

    private fun searchPosts(text: String) = viewModelScope.launch {
        if (text.isEmpty() || text.isBlank()) {
            return@launch
        }

        _state.update { it.copy(searchText = text) }
        searchInvalidatingSourceFactory.invalidate()
    }

    private suspend fun reactToPost(postId: Int, reactionToSend: Reaction) {
        api.reactToPost(postId, ReactToPostDto(reactionToSend))
            .toOperationResult { it }
            .onSuccess { reactPost ->
                modificationEvents.value += PostListEvents.Reaction(postId, reactPost.reactions)
            }
            .onError { error ->
                _state.update { it.copy(messageToShow = error) }
            }
    }

    private suspend fun revokeReaction(postId: Int) {
        api.retrieveReactionToPost(id = postId)
            .toOperationResult { it }
            .onSuccess { reactPost ->
                modificationEvents.value += PostListEvents.Reaction(postId, reactPost.reactions)
            }
            .onError { error ->
                _state.update { it.copy(messageToShow = error) }
            }
    }
}