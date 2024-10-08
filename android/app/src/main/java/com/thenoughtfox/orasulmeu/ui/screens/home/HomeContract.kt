package com.thenoughtfox.orasulmeu.ui.screens.home

import com.mapbox.geojson.Point
import org.openapitools.client.models.PostDto
import org.openapitools.client.models.PostReactionsDto

interface HomeContract {
    data class State(
        val popularPosts: List<PostDto> = emptyList(),
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val lastLocation: Point? = null,
        val chisinauCenter: Point = Point.fromLngLat(28.8306, 47.0244),
        val messageToShow: String? = null,
        val postListSorting: PostListSorting = PostListSorting.Popular,
        val searchText: String = ""
    )

    sealed interface Event {
        data class LikePost(val postId: Int) : Event
        data class DislikePost(val postId: Int) : Event
        data class RevokeReaction(val postId: Int) : Event
        data class SendReport(val postId: Int) : Event
        data class NavigateToUser(val point: Point) : Event
        data object CloseMessage : Event
        data class SelectListSorting(val sortType: PostListSorting) : Event
        data class SearchPostWithText(val searchText: String) : Event
        data object Refresh : Event
        data object RefreshNewPosts : Event
    }

    sealed interface Action {
        data class MoveToLocation(val point: Point) : Action
        data object GoToAuth: Action
    }

    sealed interface NavEvent {
        data object GoBack : NavEvent
    }

    sealed interface PostListEvents {
        data class Reaction(val postId: Int, val reactionsDto: PostReactionsDto) : PostListEvents
        data class Delete(val postId: Int) : PostListEvents
        data class Edit(val post: PostDto) : PostListEvents
    }

    enum class PostListSorting {
        Popular, New
    }
}