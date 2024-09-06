package com.thenoughtfox.orasulmeu.ui.screens.create_post

import android.net.Uri
import com.mapbox.geojson.Point
import com.thenoughtfox.orasulmeu.ui.post.utils.Media
import org.openapitools.client.models.PostDto

data class Image(
    val media: Media = Media(),
    val isUri: Boolean = true
) {
    val parsedImage = if (isUri) {
        Uri.parse(media.url)
    } else {
        media.url
    }
}

interface CreatePostContract {
    data class State(
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val images: List<Image> = listOf(),
        val removedImages: List<Image> = listOf(),
        val title: String = "",
        val content: String = "",
        val image: Image? = null,
        val address: String = "",
        val removedImage: Image? = null,
        val currentPoint: Point? = null,
        val isEdit: Boolean = false
    )

    sealed class Event {
        data class SetTitle(val title: String) : Event()
        data class SetContent(val content: String) : Event()
        data object Submit : Event()
        data class PickImages(val uris: List<Uri>) : Event()
        data class SelectImage(val image: Image) : Event()
        data class RemoveImage(val image: Image) : Event()
        data class SetAddress(val address: String, val point: Point) : Event()
        data class ShowAlert(val image: Image) : Event()
        data object DismissAlert : Event()
    }

    sealed interface NavEvent {
        data object GoBack : NavEvent
        data object GoToMedia : NavEvent
        data object GoToMapSearch : NavEvent
        data object CreatePost : NavEvent
        data object Camera : NavEvent
    }

    sealed class Action {
        data class ShowToast(val msg: String) : Action()
        data object GoBack : Action()
        data class GoBackToProfile(val post: PostDto) : Action()
        data object GoMain : Action()
    }
}
