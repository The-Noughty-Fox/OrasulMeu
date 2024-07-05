package com.thenoughtfox.orasulmeu.ui.screens.home

import com.mapbox.geojson.Point
import org.openapitools.client.models.PostDto

interface HomeContract {
    data class State(
        val postsToShow: List<PostDto> = emptyList(),
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val lastLocation: Point? = null,
        val messageToShow: String? = null
    )

    sealed interface Event {
        data class LikePost(val postId: Int) : Event
        data class DislikePost(val postId: Int) : Event
        data class RevokeReaction(val postId: Int) : Event
        data class SendReport(val postId: Int) : Event
        data class NavigateToLocation(val point: Point) : Event
        data object CloseMessage : Event
    }
}