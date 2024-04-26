package com.thenoughtfox.orasulmeu.ui.profile

import org.openapitools.client.models.PostDto

interface ProfileContract {

    data class State(
        val name: String = "",
        val imageUrl: String? = null,
        val postsCount: Int = 0,
        val reactionsCount: Int = 0,
        val ownedPost: List<PostDto> = emptyList(),
        val isEditing: Boolean = false,
    )

    sealed interface Event {
        data object OnNavigationBackPressed : Event
        data object OnSettingsPressed : Event
        data object OnProfileEditClicked : Event
    }

    sealed interface Action {

    }
}