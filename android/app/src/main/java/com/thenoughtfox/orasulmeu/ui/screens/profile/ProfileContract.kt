package com.thenoughtfox.orasulmeu.ui.screens.profile

import android.net.Uri
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
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
        val myPosts: Flow<PagingData<PostDto>> = emptyFlow(),
    )

    sealed interface Event {
        data object EditProfile : Event
        data object DiscardChanges : Event
        data object SaveChanges : Event
        data class ChangeName(val newName: String) : Event
        data class ChangePicture(val image: Uri) : Event
    }

    sealed interface Action {
        data class ShowToast(val msg: String) : Action
    }

    sealed interface NavEvent {
        data object GoToSettings : NavEvent
    }
}