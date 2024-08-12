package com.thenoughtfox.orasulmeu.ui.screens.home

import com.mapbox.geojson.Point
import org.openapitools.client.models.PostDto

interface HomeContract {
    data class State(
        val postsToShow: List<PostDto> = emptyList(),
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val lastLocation: Point? = null,
        val messageToShow: String? = null,
        val postListSorting: PostListSorting = PostListSorting.Popular,
        val searchResult: List<PostDto> = emptyList()
    )

    sealed interface Event {
        data class LikePost(val postId: Int) : Event
        data class DislikePost(val postId: Int) : Event
        data class RevokeReaction(val postId: Int) : Event
        data class SendReport(val postId: Int) : Event
        data class NavigateToLocation(val point: Point) : Event
        data object CloseMessage : Event
        data class SelectListSorting(val sortType: PostListSorting) : Event
        data class SearchPostWithText(val searchText: String) : Event
    }

    sealed interface NavEvent {
        data object GoBack : NavEvent
    }

    enum class PostListSorting {
        Popular, New
    }
}