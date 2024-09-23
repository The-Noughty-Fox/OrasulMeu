package com.thenoughtfox.orasulmeu.ui.screens.login

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.thenoughtfox.orasulmeu.R

data class State(
    val isLoadingGoogle: Boolean = false,
    val isLoadingFacebook: Boolean = false,
    val isProceed: Boolean = false,
    val isError: Boolean = false
)

sealed class Event {
    data class Auth(val type: SingInType) : Event()
    data class SendToken(val type: SingInType, val token: String) : Event()
    data class FailedAuth(val type: SingInType, val msg: String) : Event()
}

sealed class Action {
    data class Auth(val type: SingInType) : Action()
    data class ShowToast(val msg: String) : Action()
    data object Proceed : Action()
}

enum class SingInType(
    @StringRes val text: Int,
    @ColorRes val textColor: Int,
    @ColorRes val backgroundColor: Int,
    @ColorRes val imageColor: Int,
) {
    Google(R.string.sign_in_with_google, R.color.black, R.color.white, R.color.black),
    Facebook(R.string.sign_in_with_facebook, R.color.white, R.color.dark_blue, R.color.white),
}