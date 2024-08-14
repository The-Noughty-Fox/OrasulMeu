package com.thenoughtfox.orasulmeu.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.thenoughtfox.orasulmeu.net.helper.toOperationResult
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeContract.Action
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeContract.Event
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeContract.State
import com.thenoughtfox.orasulmeu.ui.screens.home.post_list.utils.CombinedPostsPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.openapitools.client.apis.PostsApi
import org.openapitools.client.models.PostDto
import org.openapitools.client.models.ReactToPostDto
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val api: PostsApi) : ViewModel() {

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
    }

    private fun handleEvents() = viewModelScope.launch {
        _event.consumeAsFlow().collect { event ->
            when (event) {
                is Event.DislikePost -> {
                    reactToPost(event.postId, ReactToPostDto.React.dislike)
                }

                is Event.LikePost -> {
                    reactToPost(event.postId, ReactToPostDto.React.like)
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
                    _state.update { it.copy(lastLocation = event.point) }
                    _action.emit(Action.MoveToLocation(event.point))
                }
            }
        }
    }

    private fun getAllPaginationPosts() {
        _state.update { it.copy(isLoading = true) }
        val newPosts: Flow<PagingData<PostDto>> = Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CombinedPostsPagingSource(api).newPostsPagingSource }
        ).flow.cachedIn(viewModelScope)

        val popularPosts: Flow<PagingData<PostDto>> = Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CombinedPostsPagingSource(api).popularPostsPagingSource }
        ).flow.cachedIn(viewModelScope)

        _state.update {
            it.copy(
                paginationNewPosts = newPosts,
                paginationPopularPosts = popularPosts,
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

    private fun applyNewSorting(newValue: HomeContract.PostListSorting) {
        _state.update { it.copy(postListSorting = newValue) }
    }

    private fun searchPosts(text: String) {
        //TODO waiting for request from Max
//        if (text.isEmpty()) {
//            _state.update { it.copy(searchResult = emptyList()) }
//        }
//
//        val posts = state.value.posts
//
//        val filteredPosts = posts.filter {
//            it.title.contains(text, ignoreCase = true) || it.content.contains(
//                text,
//                ignoreCase = true
//            )
//        }
//
//        _state.update { it.copy(searchResult = filteredPosts) }
    }

    private suspend fun reactToPost(postId: Int, reactionToSend: ReactToPostDto.React) {
        api.reactToPost(postId, ReactToPostDto(reactionToSend))
            .toOperationResult { it }
            .onSuccess { reactPost ->
//                val posts = state.value.posts.map {
//                    if (it.id == reactPost.id) {
//                        it.copy(
//                            reactions = reactPost.reactions
//                        )
//                    } else {
//                        it
//                    }
//                }
//
//                val popularPosts = state.value.popularPosts.map {
//                    if (it.id == reactPost.id) {
//                        it.copy(
//                            reactions = reactPost.reactions
//                        )
//                    } else {
//                        it
//                    }
//                }
//
//                _state.update {
//                    it.copy(posts = posts, popularPosts = popularPosts)
//                }

                val newPosts = _state.value.paginationNewPosts.map { pagingData ->
                    pagingData.filter {
                        it.id == reactPost.id
                    }.map {
                        it.copy(reactions = reactPost.reactions)
                    }
                }

                val popularPosts = _state.value.paginationPopularPosts.map { pagingData ->
                    pagingData.filter {
                        it.id == reactPost.id
                    }.map {
                        it.copy(reactions = reactPost.reactions)
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
                    pagingData.filter {
                        it.id == reactPost.id
                    }.map {
                        it.copy(reactions = reactPost.reactions)
                    }
                }

                val popularPosts = _state.value.paginationPopularPosts.map { pagingData ->
                    pagingData.filter {
                        it.id == reactPost.id
                    }.map {
                        it.copy(reactions = reactPost.reactions)
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