package com.thenoughtfox.orasulmeu.ui.screens.profile

import android.net.Uri
import com.thenoughtfox.orasulmeu.ui.post.PostContract

interface ProfileContract {

    data class State(
        val isLoading: Boolean = true,
        val name: String = "",
        val imageUrl: String? = null,
        val postsCount: Int = 0,
        val reactionsCount: Int = 0,
        val ownedPost: List<PostContract.State> = emptyList(),
        val isEditing: Boolean = false,
        val initialName: String = "",
        val initialImageUrl: String? = null,
    )

    sealed interface Event {
        data object EditProfile : Event
        data object DiscardChanges : Event
        data object SaveChanges: Event
        data class ChangeName(val newName: String) : Event
        data class ChangePicture(val image: Uri) : Event
    }

    sealed interface NavEvent {
        data object GoToSettings : NavEvent
    }
}