package com.thenoughtfox.orasulmeu.ui.profile

import android.net.Uri
import org.openapitools.client.models.PostDto

interface ProfileContract {

    data class State(
        val isLoading: Boolean = true,
        val name: String = "",
        val imageUrl: String? = null,
        val postsCount: Int = 0,
        val reactionsCount: Int = 0,
        val ownedPost: List<PostDto> = emptyList(),
        val isEditing: Boolean = false,
        val initialName: String = "",
        val initialImageUrl: String? = null,
    )

    sealed interface Event {
        data object GoToSettings : Event
        data object EditProfile : Event
        data object DiscardChanges : Event
        data object SaveChanges: Event
        data class ChangeName(val newName: String) : Event
        data class ChangePicture(val image: Uri) : Event
    }
}