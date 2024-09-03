package com.thenoughtfox.orasulmeu.ui.screens.logout

import com.thenoughtfox.orasulmeu.service.UserSharedPrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LogoutUseCaseImpl(
    private val userSharedPrefs: UserSharedPrefs,
) : LogoutUseCase {

    private var onLogoutListener: (() -> Unit)? = null

    fun setOnLogoutListener(onLogoutListener: () -> Unit) {
        this.onLogoutListener = onLogoutListener
    }

    /**
     * Logout from app and clear data.
     */
    override fun logout(msg: String?) {
        CoroutineScope(Dispatchers.Main).launch {
            userSharedPrefs.apply {
                cookies = null
                user = null
            }

            onLogoutListener?.invoke()
        }
    }
}