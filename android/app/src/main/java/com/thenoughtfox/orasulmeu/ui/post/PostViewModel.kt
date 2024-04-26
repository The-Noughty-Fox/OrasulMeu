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

@HiltViewModel(assistedFactory = PostViewModel.PostViewModelFactory::class)
class PostViewModel @AssistedInject constructor(
    private val api: PostsApi,
    @Assisted private val postModel: PostDto,
) : ViewModel() {

    @AssistedFactory
    interface PostViewModelFactory {
        fun create(dto: PostDto): PostViewModel
    }

    private val _state = MutableStateFlow(postModel.toState())
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
                            api.dislikePost(postModel.id).toOperationResult {}.onSuccess {
                                // todo soon should be converted to updated status from backend
                                val updatedReaction = PostContract.Reaction(
                                    selectedReaction = PostContract.Reactions.DISLIKE,
                                    count = postModel.dislikes + 1
                                )
                                _state.update {
                                    it.copy(
                                        reaction = updatedReaction
                                    )
                                }
                            }
                        }

                        PostContract.Event.Like -> {
                            api.likePost(postModel.id).toOperationResult {}.onSuccess {
                                // todo soon should be converted to updated status from backend
                                val updatedReaction = PostContract.Reaction(
                                    selectedReaction = PostContract.Reactions.DISLIKE,
                                    count = postModel.likes + 1
                                )
                                _state.update {
                                    it.copy(
                                        reaction = updatedReaction
                                    )
                                }
                            }
                        }

                        PostContract.Event.Report -> {
                            _actions.emit(PostContract.Action.RequestReportConfirmation)
                        }

                        PostContract.Event.RevokeReaction -> {
                            // todo remove fake delay and uncomment bottom lines
                            // val updated = postApi.revokeReaction()
//                            _state.update { it.copy(isReactionLoading = false, reaction = updated) }
                            _state.update {
                                it.copy(
                                    reaction = PostContract.Reaction(
                                        selectedReaction = PostContract.Reactions.NOTHING,
                                        count = 0
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private val _actions: MutableStateFlow<PostContract.Action?> = MutableStateFlow(null)
    val action = _actions.asStateFlow()
}