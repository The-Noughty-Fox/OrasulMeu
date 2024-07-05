package com.thenoughtfox.orasulmeu.ui.map

import com.mapbox.geojson.Point

object MapContract {

    data class State(val isLoading: Boolean = false)

    sealed interface Event {
        data class NavigateToUser(val point: Point) : Event
    }

    sealed interface Action {
        data class MoveToLocation(val point: Point) : Action
        data class ShowToast(val msg: String) : Action
    }

}