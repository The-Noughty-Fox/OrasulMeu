package com.thenoughtfox.orasulmeu.ui.screens.shared

object SharedContract {
    data class State(
        val isUserCreatedPost: Boolean = false
    )

    sealed interface Event {
        data object CreatePost : Event
        data object PostsRefreshed : Event
    }

    sealed interface Action
}