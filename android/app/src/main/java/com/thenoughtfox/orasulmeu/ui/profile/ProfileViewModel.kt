package com.thenoughtfox.orasulmeu.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import com.thenoughtfox.orasulmeu.ui.profile.ProfileContract.*
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor () : ViewModel() {
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
    }

    private fun handleEvent(e: Event) {
        when (e) {
            Event.OnNavigationBackPressed -> TODO()
            Event.OnProfileEditClicked -> TODO()
            Event.OnSettingsPressed -> TODO()
        }
    }
}