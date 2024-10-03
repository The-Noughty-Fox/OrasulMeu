package com.thenoughtfox.orasulmeu.ui.screens.create_post.map

import android.location.Location
import com.mapbox.geojson.Point
import com.mapbox.search.ReverseGeoOptions
import com.mapbox.search.result.SearchSuggestion

data class State(
    val isLoading: Boolean = false,
    val suggestions: List<SearchSuggestion> = listOf(),
    val isSuggestionListShow: Boolean = false,
    val lastLocation: Point? = null,
    val chisinauCenter: Point = Point.fromLngLat(28.8306, 47.0244),
    val address: String = "",
    val currentPoint: Point = Point.fromLngLat(0.0, 0.0),
    val searchText: String = ""
)

sealed class Event {
    data class NavigateToUser(val location: Location) : Event()
    data class MoveToLocation(val point: Point, val address: String) : Event()
    data class DoOnTextLocationChanged(val text: String) : Event()
    data class OnCameraTrackingDismissed(val geo: ReverseGeoOptions) : Event()
    data class OnSearchSuggestionClicked(val suggestion: SearchSuggestion) : Event()
    data object ClearSearchText : Event()
}

sealed class Action {
    data class MoveToLocation(val point: Point) : Action()
    data class ShowToast(val msg: String) : Action()
}