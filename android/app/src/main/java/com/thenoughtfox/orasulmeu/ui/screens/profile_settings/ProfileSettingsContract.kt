package com.thenoughtfox.orasulmeu.ui.screens.profile_settings

object ProfileSettingsContract {
    data class State(
        val isLoading: Boolean = false,
        val isError: Boolean = false,
    )

    sealed interface Event {
        data object Logout : Event
        data object DeleteAccount : Event
    }

    sealed interface Action {
        data class ShowToast(val msg: String) : Action
        data object Logout : Action
    }

    sealed interface NavEvent {
        data object GoBack : NavEvent
    }
}