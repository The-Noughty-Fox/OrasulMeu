package com.thenoughtfox.orasulmeu.ui.login.presentation

data class State(
    val isLoadingGoogle: Boolean = false,
    val isLoadingFacebook: Boolean = false,
    val isError: Boolean = false
)

sealed class Event {
    data object AuthGoogle: Event()
}

sealed class Action {

}