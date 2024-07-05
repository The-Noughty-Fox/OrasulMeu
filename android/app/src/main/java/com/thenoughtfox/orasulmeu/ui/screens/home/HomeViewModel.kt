package com.thenoughtfox.orasulmeu.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thenoughtfox.orasulmeu.net.helper.toOperationResult
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeContract.Event
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.openapitools.client.apis.PostsApi
import org.openapitools.client.models.PostDto
import org.openapitools.client.models.PostReactionsDto
import org.openapitools.client.models.ReactToPostDto
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: PostsApi
) : ViewModel() {
    private val _state: MutableStateFlow<State> =
        MutableStateFlow(State())
    val state = _state.asStateFlow()

    private val _event: MutableStateFlow<Event?> = MutableStateFlow(null)
    suspend fun sendEvent(a: Event) {
        _event.emit(a)
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

            _event.collect { action ->
                when (action) {
                    is Event.DislikePost -> {
                        dislikePost(action.postId)
                    }

                    is Event.LikePost -> {
                        likePost(action.postId)
                    }

                    is Event.RevokeReaction -> {
                        revokeReaction(action.postId)
                    }

                    is Event.SendReport -> {
                        _state.update { it.copy(messageToShow = "Feature will be implemented soon") }
                    }

                    is Event.NavigateToLocation -> {
                        _state.update { it.copy(lastLocation = action.point) }
                    }

                    Event.CloseMessage -> {
                        _state.update { it.copy(messageToShow = null) }
                    }

                    null -> Unit
                }
            }
        }
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