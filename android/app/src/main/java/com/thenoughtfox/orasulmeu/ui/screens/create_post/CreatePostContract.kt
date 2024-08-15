package com.thenoughtfox.orasulmeu.ui.screens.create_post

import android.net.Uri
import com.mapbox.geojson.Point

interface CreatePostContract {
    data class State(
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val images: List<Uri> = listOf(),
        val title: String = "",
        val description: String = "",
        val image: Uri? = null,
        val address: String = "",
        val removedUri: Uri? = null,
        val currentPoint: Point? = null
    )

    sealed class Event {
        data class SetTitle(val title: String) : Event()
        data class SetDescription(val desc: String) : Event()
        data object Submit : Event()
        data class PickImages(val uris: List<Uri>) : Event()
        data class SelectImage(val image: Uri) : Event()
        data class RemoveImage(val image: Uri) : Event()
        data class SetAddress(val address: String, val point: Point) : Event()
        data class ShowAlert(val uri: Uri) : Event()
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
        data object GoMain : Action()
    }
}
