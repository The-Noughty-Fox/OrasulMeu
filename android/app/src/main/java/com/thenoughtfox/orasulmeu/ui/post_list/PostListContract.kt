package com.thenoughtfox.orasulmeu.ui.post_list

import com.thenoughtfox.orasulmeu.ui.post.PostContract

/**
 * @author Knurenko Bogdan 14.06.2024
 */
interface PostListContract {
    data class State(
        val isLoading: Boolean = true,
        val list: List<PostContract.State> = emptyList(),
        val messageToShow: String? = null
    )

    sealed interface Action {
        data object Refresh : Action
        data class LikePost(val postId: Int) : Action
        data class DislikePost(val postId: Int) : Action
        data class RevokeReaction(val postId: Int) : Action
        data class SendReport(val postId: Int) : Action
        data object CloseMessage : Action
    }
}