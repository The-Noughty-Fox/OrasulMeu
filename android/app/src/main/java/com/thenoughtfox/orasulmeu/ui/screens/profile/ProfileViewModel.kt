package com.thenoughtfox.orasulmeu.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thenoughtfox.orasulmeu.service.UserSharedPrefs
import com.thenoughtfox.orasulmeu.ui.screens.profile.ProfileContract.Event
import com.thenoughtfox.orasulmeu.ui.screens.profile.ProfileContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(userSharedPrefs: UserSharedPrefs) : ViewModel() {
    private val _state: MutableStateFlow<State> = MutableStateFlow(State())
    private val _events: MutableStateFlow<Event?> = MutableStateFlow(null)

    val state = _state.asStateFlow()
    fun sendEvent(event: Event) {
        _events.update { event }
    }

    init {
        viewModelScope.launch {
            _events.collect { e -> e?.let { handleEvent(it) } }
        }

        userSharedPrefs.user?.let { user ->
            _state.update {
                it.copy(
                    isLoading = false,
                    name = user.userName ?: "empty name",
                    imageUrl = user.socialProfilePictureUrl,
                )
            }
        }
    }

    private fun handleEvent(e: Event) {
        when (e) {
            Event.EditProfile -> {
                _state.update {
                    it.copy(
                        isEditing = true,
                        initialName = it.name,
                        initialImageUrl = it.imageUrl
                    )
                }
            }

            is Event.ChangeName -> {
                _state.update { it.copy(name = e.newName) }
            }

            is Event.ChangePicture -> {
                _state.update { it.copy(imageUrl = e.image.toString()) }
            }

            Event.SaveChanges -> {
                _state.update { it.copy(isEditing = false) }
            }

            Event.DiscardChanges -> {
                _state.update {
                    it.copy(
                        isEditing = false,
                        name = it.initialName,
                        imageUrl = it.initialImageUrl
                    )
                }
            }
        }
    }
}