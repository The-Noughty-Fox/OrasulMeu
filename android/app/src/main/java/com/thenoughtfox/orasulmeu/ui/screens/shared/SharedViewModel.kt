package com.thenoughtfox.orasulmeu.ui.screens.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thenoughtfox.orasulmeu.ui.screens.shared.SharedContract.Event
import com.thenoughtfox.orasulmeu.ui.screens.shared.SharedContract.Action
import com.thenoughtfox.orasulmeu.ui.screens.shared.SharedContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {

    private val _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private val _event: Channel<Event> = Channel(Channel.UNLIMITED)

    private val _action = MutableSharedFlow<Action>()
    val action: SharedFlow<Action> = _action

    suspend fun sendEvent(a: Event) {
        _event.send(a)
    }

    init {
        handleEvents()
    }

    private fun handleEvents() = viewModelScope.launch {
        _event.consumeAsFlow().collect { event ->
            when (event) {
                Event.UpdatePosts -> _state.update { it.copy(isPostUpdated = true) }
                Event.PostsRefreshed -> _state.update { it.copy(isPostUpdated = false) }
                is Event.UpdatePost -> _state.update {
                    it.copy(isPostUpdated = true, post = event.postDto)
                }
            }
        }
    }
}