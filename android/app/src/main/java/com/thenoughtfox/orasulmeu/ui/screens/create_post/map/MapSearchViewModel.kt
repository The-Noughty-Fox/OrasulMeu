package com.thenoughtfox.orasulmeu.ui.screens.create_post.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.mapbox.geojson.Point
import com.mapbox.search.ResponseInfo
import com.mapbox.search.ReverseGeoOptions
import com.mapbox.search.SearchCallback
import com.mapbox.search.SearchEngine
import com.mapbox.search.SearchEngineSettings
import com.mapbox.search.SearchOptions
import com.mapbox.search.SearchSelectionCallback
import com.mapbox.search.SearchSuggestionsCallback
import com.mapbox.search.common.IsoCountryCode
import com.mapbox.search.common.IsoLanguageCode
import com.mapbox.search.result.SearchAddress
import com.mapbox.search.result.SearchResult
import com.mapbox.search.result.SearchSuggestion
import com.thenoughtfox.orasulmeu.utils.toPoint
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
class MapSearchViewModel @Inject constructor(
) : ViewModel() {

    val event = Channel<Event>(Channel.UNLIMITED)
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()
    private val viewState
        get() = state.value

    private val _action = MutableSharedFlow<Action>()
    val action: SharedFlow<Action> = _action

    private val searchOptions by lazy {
        SearchOptions(
            limit = 10,
            proximity = viewState.currentPoint,
            countries = listOf(IsoCountryCode.MOLDOVA),
            languages = listOf(IsoLanguageCode("en"), IsoLanguageCode("ro"), IsoLanguageCode("ru"))
        )
    }

    private val searchEngine: SearchEngine by lazy {
        SearchEngine.createSearchEngineWithBuiltInDataProviders(
            SearchEngineSettings()
        )
    }

    init {
        handleEvents()
    }

    private fun handleEvents() = viewModelScope.launch {
        event.consumeAsFlow().collect { event ->
            when (event) {
                is Event.NavigateToUser -> {
                    val point = Point.fromLngLat(event.location.longitude, event.location.latitude)
                    searchEngine.search(
                        ReverseGeoOptions(center = point), reverseSearchCallback
                    )

                    _action.emit(Action.MoveToLocation(point))
                }

                is Event.MoveToLocation -> moveToLocation(event.point, event.address)
                is Event.DoOnTextLocationChanged -> {
                    _state.update { it.copy(searchText = event.text) }
                    searchEngine.search(event.text, searchOptions, searchCallback)
                }

                is Event.OnCameraTrackingDismissed -> {
                    _state.update { it.copy(searchText = "") }
                    searchEngine.search(event.geo, reverseSearchCallback)
                }

                is Event.OnSearchSuggestionClicked -> {
                    val shortAddress =
                        event.suggestion.address?.formattedAddress(SearchAddress.FormatStyle.Short)
                    _state.update { it.copy(searchText = shortAddress.toString()) }
                    searchEngine.select(event.suggestion, selectCallback)
                }

                Event.ClearSearchText -> _state.update { it.copy(searchText = "", isSuggestionListShow = false) }
            }
        }
    }

    private fun moveToLocation(point: Point, address: String) = viewModelScope.launch {
        _state.update {
            it.copy(
                suggestions = listOf(),
                isSuggestionListShow = false,
                address = address,
                currentPoint = point
            )
        }

        _action.emit(Action.MoveToLocation(point))
    }

    private val searchCallback = object : SearchSuggestionsCallback {
        override fun onSuggestions(
            suggestions: List<SearchSuggestion>,
            responseInfo: ResponseInfo
        ) {
            getSuggestions(suggestions)
        }

        override fun onError(e: Exception) {
        }
    }

    private fun getSuggestions(suggestions: List<SearchSuggestion>) {
        _state.update {
            it.copy(
                suggestions = suggestions,
                isSuggestionListShow = suggestions.isNotEmpty()
            )
        }
    }

    private val reverseSearchCallback = object : SearchCallback {
        override fun onResults(results: List<SearchResult>, responseInfo: ResponseInfo) {
            if (results.isNotEmpty()) {
                getPointsAddress(results.first())
            }
        }

        override fun onError(e: Exception) {}
    }

    private fun getPointsAddress(result: SearchResult) {
        val address = (result.address?.street ?: "") + " " + (result.address?.houseNumber ?: "")
        _state.update {
            it.copy(
                address = address.trim(),
                currentPoint = LatLng(
                    result.coordinate.latitude(),
                    result.coordinate.longitude()
                ).toPoint()
            )
        }
    }

    private val selectCallback = object : SearchSelectionCallback {
        override fun onResult(
            suggestion: SearchSuggestion,
            result: SearchResult,
            responseInfo: ResponseInfo
        ) {
            val street = if (suggestion.address?.street.isNullOrEmpty()) {
                ""
            } else {
                ", ${suggestion.address?.street}"
            }

            val address = "${suggestion.name}$street"
            moveToLocation(result.coordinate, address)
        }

        override fun onSuggestions(
            suggestions: List<SearchSuggestion>, responseInfo: ResponseInfo
        ) {
        }

        override fun onResults(
            suggestion: SearchSuggestion,
            results: List<SearchResult>,
            responseInfo: ResponseInfo
        ) {
        }

        override fun onError(e: Exception) {}
    }

}