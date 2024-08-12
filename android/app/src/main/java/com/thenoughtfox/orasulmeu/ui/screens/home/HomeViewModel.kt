package com.thenoughtfox.orasulmeu.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thenoughtfox.orasulmeu.net.helper.toOperationResult
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeContract.Event
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.openapitools.client.apis.PostsApi
import org.openapitools.client.models.PostDto
import org.openapitools.client.models.PostReactionsDto
import org.openapitools.client.models.ReactToPostDto
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val api: PostsApi) : ViewModel() {

    private val _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private val _event: Channel<Event> = Channel(Channel.UNLIMITED)

    suspend fun sendEvent(a: Event) {
        _event.send(a)
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoading = true) }
            api.getPosts().toOperationResult { it }.onSuccess { response ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        postsToShow = response.data ?: emptyList()
                    )
                }
            }

            _event.consumeAsFlow().collect { event ->
                when (event) {
                    is Event.DislikePost -> {
                        dislikePost(event.postId)
                    }

                    is Event.LikePost -> {
                        likePost(event.postId)
                    }

                    is Event.RevokeReaction -> {
                        revokeReaction(event.postId)
                    }

                    is Event.SendReport -> {
                        _state.update { it.copy(messageToShow = "Feature will be implemented soon") }
                    }

                    is Event.NavigateToLocation -> {
                        _state.update { it.copy(lastLocation = event.point) }
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
                }
            }
        }
    }

    private fun applyNewSorting(newValue: HomeContract.PostListSorting) {
        val posts = state.value.postsToShow
        val sortedList = when (newValue) {
            HomeContract.PostListSorting.Popular -> posts.sortedByDescending {
                (it.reactions.like + it.reactions.dislike)
            }

            HomeContract.PostListSorting.New -> posts.sortedBy {
                it.id   // todo change to createdAt
            }
        }

        _state.update {
            it.copy(
                postListSorting = newValue,
                postsToShow = sortedList
            )
        }
    }

    private fun searchPosts(text: String) {
        if (text.isEmpty()) {
            _state.update { it.copy(searchResult = emptyList()) }
        }

        val posts = state.value.postsToShow

        val filteredPosts = posts.filter {
            it.title.contains(text, ignoreCase = true)
                    || it.content.contains(text, ignoreCase = true)
        }

        _state.update { it.copy(searchResult = filteredPosts) }
    }

    private suspend fun likePost(postId: Int) {
        updateListItem(postId) {
            it.copy(
                reactions = it.reactions.copy(
                    like = it.reactions.like + 1,
                    userReaction = PostReactionsDto.UserReaction.like,
                )
            )
        }
        api.reactToPost(postId, ReactToPostDto(ReactToPostDto.React.like))
    }

    private suspend fun dislikePost(postId: Int) {
        updateListItem(postId) {
            it.copy(
                reactions = it.reactions.copy(
                    like = it.reactions.dislike + 1,
                    userReaction = PostReactionsDto.UserReaction.dislike,
                )
            )
        }
        api.reactToPost(postId, ReactToPostDto(ReactToPostDto.React.dislike))
    }

    private suspend fun revokeReaction(postId: Int) {
        val post = _state.value.postsToShow.find { it.id == postId } ?: return

        val reactionToSend = when (post.reactions.userReaction) {
            PostReactionsDto.UserReaction.like -> ReactToPostDto.React.dislike
            PostReactionsDto.UserReaction.dislike -> ReactToPostDto.React.like
            null -> null
        } ?: return

        var likes = post.reactions.like
        if (post.reactions.userReaction == PostReactionsDto.UserReaction.like) likes -= 1

        var dislikes = post.reactions.dislike
        if (post.reactions.userReaction == PostReactionsDto.UserReaction.dislike) dislikes -= 1

        updateListItem(postId) {
            it.copy(
                reactions = PostReactionsDto(
                    dislike = dislikes,
                    like = likes,
                    userReaction = null
                )
            )
        }

        api.reactToPost(postId, ReactToPostDto(reactionToSend))
    }

    private fun updateListItem(id: Int, morph: (PostDto) -> PostDto) {
        _state.update { s ->
            s.copy(postsToShow = s.postsToShow.map { if (it.id == id) morph(it) else it })
        }
    }
}