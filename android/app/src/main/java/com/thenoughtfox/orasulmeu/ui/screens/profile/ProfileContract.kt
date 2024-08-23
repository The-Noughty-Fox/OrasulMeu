package com.thenoughtfox.orasulmeu.ui.screens.profile

import android.net.Uri
import com.thenoughtfox.orasulmeu.ui.post.utils.Post
import org.openapitools.client.models.PostDto

interface ProfileContract {

    data class State(
        val isLoading: Boolean = true,
        val name: String = "",
        val imageUrl: String? = null,
        val postsCount: Int = 0,
        val reactionsCount: Int = 0,
        val isEditing: Boolean = false,
        val newName: String? = null,
        val newImageUri: Uri? = null,
        val isRefreshing: Boolean = false
    )

    sealed interface Event {
        data object LoadProfile : Event
        data object EditProfile : Event
        data object DiscardChanges : Event
        data object SaveChanges : Event
        data class ChangeName(val newName: String) : Event
        data class ChangePicture(val image: Uri) : Event
        data object EnterSettings : Event
        data object Refresh : Event
        data class RefreshEditedPost(val post: PostDto) : Event
        data class EditPost(val postId: Int) : Event
        data class DeletePost(val postId: Int) : Event
    }

    sealed interface Action {
        data class ShowToast(val msg: String) : Action
        data class GoEditPost(val post: Post) : Action
    }

    sealed interface NavEvent {
        data object GoToSettings : NavEvent
    }
}