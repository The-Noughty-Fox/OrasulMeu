package com.thenoughtfox.orasulmeu.ui.screens.shared

import org.openapitools.client.models.PostDto

object SharedContract {
    data class State(
        val isPostUpdated: Boolean = false,
        val post: PostDto? = null
    )

    sealed interface Event {
        data object UpdatePosts : Event
        data class UpdatePost(val postDto: PostDto) : Event
        data object PostsRefreshed : Event
    }

    sealed interface Action
}