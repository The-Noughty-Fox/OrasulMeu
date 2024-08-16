package com.thenoughtfox.orasulmeu.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeContract.State
import com.thenoughtfox.orasulmeu.ui.screens.home.utils.CombinedPostsPagingSource
import com.thenoughtfox.orasulmeu.ui.screens.home.utils.PostType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
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

    suspend fun sendEvent(a: Event) {
        _event.send(a)
    }

    init {
        handleEvents()
        getAllPopularPosts()
        getAllPaginationPosts()
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
                    getAllPaginationPosts()
                    _state.update { it.copy(isRefreshing = false) }
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

    private fun getAllPaginationPosts() = viewModelScope.launch {
        val newPosts: Flow<PagingData<PostDto>> = Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CombinedPostsPagingSource(api).getPostsPagingSource(
                    type = PostType.NEW
                )
            }
        ).flow.cachedIn(viewModelScope)

        val popularPosts: Flow<PagingData<PostDto>> = Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CombinedPostsPagingSource(api).getPostsPagingSource(
                    type = PostType.POPULAR
                )
            }
        ).flow.cachedIn(viewModelScope)

        _state.update {
            it.copy(
                paginationNewPosts = newPosts,
                paginationPopularPosts = popularPosts
            )
        }
    }

    private fun applyNewSorting(newValue: HomeContract.PostListSorting) {
        _state.update { it.copy(postListSorting = newValue) }
    }

    private fun searchPosts(text: String) = viewModelScope.launch {
        if (text.isEmpty()) {
            _state.update { it.copy(searchResult = emptyFlow()) }
        }

        val posts: Flow<PagingData<PostDto>> = Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CombinedPostsPagingSource(api).getPostsPagingSource(
                    type = PostType.SEARCH, phrase = text
                )
            }
        ).flow.cachedIn(viewModelScope)

        _state.update { it.copy(searchResult = posts) }
    }

    private suspend fun reactToPost(postId: Int, reactionToSend: Reaction) {
        api.reactToPost(postId, ReactToPostDto(reactionToSend))
            .toOperationResult { it }
            .onSuccess { reactPost ->
                val newPosts = _state.value.paginationNewPosts.map { pagingData ->
                    pagingData.map {
                        if (it.id == reactPost.id) {
                            it.copy(reactions = reactPost.reactions)
                        } else {
                            it
                        }
                    }
                }

                val popularPosts = _state.value.paginationPopularPosts.map { pagingData ->
                    pagingData.map {
                        if (it.id == reactPost.id) {
                            it.copy(reactions = reactPost.reactions)
                        } else {
                            it
                        }
                    }
                }

                _state.update {
                    it.copy(paginationNewPosts = newPosts, paginationPopularPosts = popularPosts)
                }
            }
            .onError { error ->
                _state.update { it.copy(messageToShow = error) }
            }
    }

    private suspend fun revokeReaction(postId: Int) {
        api.retrieveReactionToPost(id = postId)
            .toOperationResult { it }
            .onSuccess { reactPost ->
                val newPosts = _state.value.paginationNewPosts.map { pagingData ->
                    pagingData.map {
                        if (it.id == reactPost.id) {
                            it.copy(reactions = reactPost.reactions)
                        } else {
                            it
                        }
                    }
                }

                val popularPosts = _state.value.paginationPopularPosts.map { pagingData ->
                    pagingData.map {
                        if (it.id == reactPost.id) {
                            it.copy(reactions = reactPost.reactions)
                        } else {
                            it
                        }
                    }
                }

                _state.update {
                    it.copy(paginationNewPosts = newPosts, paginationPopularPosts = popularPosts)
                }
            }
            .onError { error ->
                _state.update { it.copy(messageToShow = error) }
            }
    }
}