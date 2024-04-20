package com.thenoughtfox.orasulmeu.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import com.thenoughtfox.orasulmeu.ui.map.MapContract.Action
import com.thenoughtfox.orasulmeu.ui.map.MapContract.Event
import com.thenoughtfox.orasulmeu.ui.map.MapContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {

    val event = Channel<Event>(Channel.UNLIMITED)
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()
    private val viewState
        get() = state.value

    private val _action = MutableSharedFlow<Action>()
    val action: SharedFlow<Action> = _action


    init {
        handleEvents()
    }

    private fun handleEvents() = viewModelScope.launch {
        event.consumeAsFlow().collect { event ->
            when (event) {
                is Event.NavigateToPlayer -> {
                    _action.emit(Action.MoveToLocation(event.point))
                }
            }
        }
    }
}