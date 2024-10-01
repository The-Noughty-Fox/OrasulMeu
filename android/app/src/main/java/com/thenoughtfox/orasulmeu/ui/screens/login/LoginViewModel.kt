package com.thenoughtfox.orasulmeu.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thenoughtfox.orasulmeu.net.helper.toOperationResult
import com.thenoughtfox.orasulmeu.net.model.User
import com.thenoughtfox.orasulmeu.service.UserSharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.openapitools.client.apis.AuthApi
import org.openapitools.client.models.ApiBodyWithToken
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authApi: AuthApi,
    private val userSharedPrefs: UserSharedPrefs,
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
                is Event.Auth -> {
                    if (state.value.isLoadingGoogle ||
                        state.value.isLoadingFacebook ||
                        state.value.isProceed
                    ) {
                        return@collect
                    }

                    when (event.type) {
                        SingInType.Google -> _state.update { it.copy(isLoadingGoogle = true) }
                        SingInType.Facebook -> _state.update { it.copy(isLoadingFacebook = true) }
                    }

                    _action.emit(Action.Auth(event.type))
                }

                is Event.SendToken -> {
                    when (event.type) {
                        SingInType.Google -> {
                            authApi.authWithGoogle(ApiBodyWithToken(event.token))
                                .toOperationResult { it }
                                .onSuccess {
                                    userSharedPrefs.user =
                                        User(
                                            id = it.id,
                                            email = it.email,
                                            socialProfilePictureUrl = it.socialProfilePictureUrl,
                                            userName = it.username
                                        )

                                    _state.update { uiState ->
                                        uiState.copy(
                                            isLoadingGoogle = false, isProceed = true
                                        )
                                    }

                                    _action.emit(Action.Proceed)
                                }
                                .onError {
                                    _state.update { uiState ->
                                        uiState.copy(
                                            isLoadingGoogle = false, isProceed = false
                                        )
                                    }

                                    _action.emit(Action.ShowToast(it))
                                }
                        }

                        SingInType.Facebook -> {
                            authApi.authWithFacebook(ApiBodyWithToken(event.token))
                                .toOperationResult { it }
                                .onSuccess {
                                    userSharedPrefs.user =
                                        User(
                                            id = it.id,
                                            email = it.email,
                                            socialProfilePictureUrl = it.socialProfilePictureUrl,
                                            userName = it.username
                                        )

                                    _state.update { uiState ->
                                        uiState.copy(
                                            isLoadingFacebook = false, isProceed = true
                                        )
                                    }

                                    _action.emit(Action.Proceed)
                                }
                                .onError {
                                    _state.update { uiState ->
                                        uiState.copy(
                                            isLoadingFacebook = false, isProceed = false
                                        )
                                    }
                                    _action.emit(Action.ShowToast(it))
                                }
                        }
                    }
                }

                is Event.FailedAuth -> {
                    when (event.type) {
                        SingInType.Google -> _state.update { it.copy(isLoadingGoogle = false) }
                        SingInType.Facebook -> _state.update { it.copy(isLoadingFacebook = false) }
                    }

                    _action.emit(Action.ShowToast(event.msg))
                }

                Event.Skip -> _action.emit(Action.Proceed)
            }
        }
    }
}