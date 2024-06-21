package com.thenoughtfox.orasulmeu.ui.profile_settings

object ProfileSettingsContract {
    data class State(
        val isLoading: Boolean = false,
        val isError: Boolean = false,
    )

    sealed interface Event {
        data object Back : Event
        data object Logout : Event
        data object DeleteAccount : Event
    }

    sealed interface Action {
        data class ShowToast(val msg: String) : Action
    }
}