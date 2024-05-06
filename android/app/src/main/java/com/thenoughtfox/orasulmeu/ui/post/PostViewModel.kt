package com.thenoughtfox.orasulmeu.ui.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thenoughtfox.orasulmeu.net.helper.toOperationResult
import com.thenoughtfox.orasulmeu.ui.post.utils.PostDtoToStateMapper.toState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.openapitools.client.apis.PostsApi
import org.openapitools.client.models.PostDto
import org.openapitools.client.models.ReactToPostDto

@HiltViewModel(assistedFactory = PostViewModel.PostViewModelFactory::class)
class PostViewModel @AssistedInject constructor(
    private val api: PostsApi,
    @Assisted private val postDto: PostDto,
) : ViewModel() {

    @AssistedFactory
    interface PostViewModelFactory {
        fun create(dto: PostDto): PostViewModel
    }

    private val _state = MutableStateFlow(postDto.toState())
    val state = _state.asStateFlow()

    private val eventFlow: MutableStateFlow<PostContract.Event?> = MutableStateFlow(null)
    fun emitEvent(e: PostContract.Event) = viewModelScope.launch { eventFlow.emit(e) }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            eventFlow.collect { nullable ->
                nullable?.let { event ->
                    when (event) {
                        PostContract.Event.ConfirmReport -> {
//                            api.reportPost(postModel.id)
//                            .toOperationResult {}.onSuccess {
//                                _actions.emit(PostContract.Action.ShowReportSubmitted)
//                            }.onError {
//                                _actions.emit(PostContract.Action.ShowReportSendingFailed)
//                            }
                        }

                        PostContract.Event.Dislike -> {
                            val updatedReaction = PostContract.Reaction(
                                selectedReaction = PostContract.Reactions.DISLIKE,
                                count = postDto.reactions.dislike + 1
                            )
                            _state.update {
                                it.copy(reaction = updatedReaction)
                            }

                            sendReaction(ReactToPostDto.React.dislike)
                        }

                        PostContract.Event.Like -> {
                            val updatedReaction = PostContract.Reaction(
                                selectedReaction = PostContract.Reactions.DISLIKE,
                                count = postDto.reactions.like + 1
                            )
                            _state.update {
                                it.copy(
                                    reaction = updatedReaction
                                )
                            }

                            sendReaction(ReactToPostDto.React.like)
                        }

                        PostContract.Event.Report -> {
                            _actions.emit(PostContract.Action.RequestReportConfirmation)
                        }

                        PostContract.Event.RevokeReaction -> {
                            val updatedReaction = PostContract.Reaction(
                                selectedReaction = PostContract.Reactions.NOTHING,
                                count = 0
                            )
                            _state.update {
                                it.copy(
                                    reaction = updatedReaction
                                )
                            }

                            when (_state.value.reaction.selectedReaction) {
                                PostContract.Reactions.LIKE -> ReactToPostDto.React.dislike
                                PostContract.Reactions.DISLIKE -> ReactToPostDto.React.like
                                PostContract.Reactions.NOTHING -> null
                            }?.let { sendReaction(it) }
                        }
                    }
                }
            }
        }
    }

    private suspend fun sendReaction(reaction: ReactToPostDto.React) {
        api.reactToPost(postDto.id, ReactToPostDto(react = reaction))
            .toOperationResult { it }.onSuccess { dto ->
                _state.update { dto.toState() }
            }
    }

    private val _actions: MutableStateFlow<PostContract.Action?> = MutableStateFlow(null)
    val action = _actions.asStateFlow()
}