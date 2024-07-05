package com.thenoughtfox.orasulmeu.ui.screens.post_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thenoughtfox.orasulmeu.net.helper.toOperationResult
import com.thenoughtfox.orasulmeu.ui.post.PostContract
import com.thenoughtfox.orasulmeu.ui.post.utils.PostDtoToStateMapper.toState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.openapitools.client.apis.PostsApi
import org.openapitools.client.models.ReactToPostDto
import javax.inject.Inject

/**
 * @author Knurenko Bogdan 14.06.2024
 */

@HiltViewModel
class PostListViewModel @Inject constructor(
    private val api: PostsApi
) : ViewModel() {

    private val _state: MutableStateFlow<PostListContract.State> =
        MutableStateFlow(PostListContract.State())
    val state = _state.asStateFlow()

    private val _event: MutableStateFlow<PostListContract.Event?> = MutableStateFlow(null)
    suspend fun sendEvent(a: PostListContract.Event) {
        _event.emit(a)
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {

            _state.update { it.copy(isLoading = true) }
            api.getPosts().toOperationResult { it }.onSuccess { response ->
                _state.update {
                    it.copy(isLoading = false,
                        list = response.data?.map { item -> item.toState() }
                            ?: emptyList())
                }
            }

            _event.collect { action ->
                when (action) {
                    is PostListContract.Event.DislikePost -> {
                        dislikePost(action.postId)
                    }

                    is PostListContract.Event.LikePost -> {
                        likePost(action.postId)
                    }

                    is PostListContract.Event.RevokeReaction -> {
                        revokeReaction(action.postId)
                    }

                    is PostListContract.Event.SendReport -> {
                        _state.update { it.copy(messageToShow = "Feature will be implemented soon") }
                    }

                    PostListContract.Event.CloseMessage -> {
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
                reaction = it.reaction.copy(
                    selectedReaction = PostContract.Reactions.LIKE,
                    likes = it.reaction.likes + 1
                )
            )
        }
        api.reactToPost(postId, ReactToPostDto(ReactToPostDto.React.like))
    }

    private suspend fun dislikePost(postId: Int) {
        updateListItem(postId) {
            it.copy(
                reaction = it.reaction.copy(
                    selectedReaction = PostContract.Reactions.DISLIKE,
                    dislikes = it.reaction.dislikes + 1
                )
            )
        }
        api.reactToPost(postId, ReactToPostDto(ReactToPostDto.React.dislike))
    }

    private suspend fun revokeReaction(postId: Int) {
        val post = _state.value.list.find { it.id == postId } ?: return

        val reactionToSend = when (post.reaction.selectedReaction) {
            PostContract.Reactions.LIKE -> ReactToPostDto.React.dislike
            PostContract.Reactions.DISLIKE -> ReactToPostDto.React.like
            PostContract.Reactions.NOTHING -> null
        } ?: return

        var likes = post.reaction.likes
        if (post.reaction.selectedReaction == PostContract.Reactions.LIKE) likes -= 1

        var dislikes = post.reaction.dislikes
        if (post.reaction.selectedReaction == PostContract.Reactions.DISLIKE) dislikes -= 1

        updateListItem(postId) {
            it.copy(
                reaction = PostContract.Reaction(
                    selectedReaction = PostContract.Reactions.NOTHING,
                    likes = likes,
                    dislikes = dislikes
                )
            )
        }

        api.reactToPost(postId, ReactToPostDto(reactionToSend))
    }

    private fun updateListItem(id: Int, morph: (PostContract.State) -> PostContract.State) {
        _state.update { s ->
            s.copy(list = s.list.map { if (it.id == id) morph(it) else it })
        }
    }
}