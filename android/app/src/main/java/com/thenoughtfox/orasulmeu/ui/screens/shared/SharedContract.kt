package com.thenoughtfox.orasulmeu.ui.screens.shared

import org.openapitools.client.models.PostDto

object SharedContract {
    data class State(
        val isPostUpdated: Boolean = false,
        val post: PostDto? = null,
        val isAnonymous: Boolean = false
    )

    sealed interface Event {
        data object UpdatePosts : Event
        data class UpdatePost(val postDto: PostDto) : Event
        data object PostsRefreshed : Event
        data class SetAnonymousUser(val isAnonymous: Boolean) : Event
    }

    sealed interface Action
}