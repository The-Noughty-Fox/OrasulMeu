package com.thenoughtfox.orasulmeu.ui.profile_settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import com.thenoughtfox.orasulmeu.navigation.Screens
import com.thenoughtfox.orasulmeu.net.helper.toOperationResult
import com.thenoughtfox.orasulmeu.service.UserSharedPrefs
import com.thenoughtfox.orasulmeu.ui.profile_settings.ProfileSettingsContract.Action
import com.thenoughtfox.orasulmeu.ui.profile_settings.ProfileSettingsContract.Event
import com.thenoughtfox.orasulmeu.ui.profile_settings.ProfileSettingsContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.openapitools.client.apis.UsersApi
import javax.inject.Inject

@HiltViewModel
class ProfileSettingsViewModel @Inject constructor(
    private val router: Router,
    private val userSharedPrefs: UserSharedPrefs,
    private val usersApi: UsersApi
) : ViewModel() {

    val event = Channel<Event>(Channel.UNLIMITED)

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private val _action = MutableSharedFlow<Action>()
    val action: SharedFlow<Action> = _action

    init {
        handleEvents()
    }

    private fun handleEvents() = viewModelScope.launch {
        event.consumeAsFlow().collect { event ->
            when (event) {
                Event.Back -> router.backTo(null)
                Event.DeleteAccount -> deleteAccount()
                Event.Logout -> logout()
            }
        }
    }

    private fun logout() {
        userSharedPrefs.apply {
            cookies = null
            user = null
        }

        router.newRootScreen(Screens.loginScreen)
    }

    private fun deleteAccount() = viewModelScope.launch {
        val id = userSharedPrefs.user?.id ?: return@launch
        usersApi.remove(id = id.toString())
            .toOperationResult { it }
            .onSuccess {
                userSharedPrefs.apply {
                    cookies = null
                    user = null
                }

                router.newRootScreen(Screens.loginScreen)
            }
            .onError {
                _state.update { it.copy(isError = true) }
            }
    }
}