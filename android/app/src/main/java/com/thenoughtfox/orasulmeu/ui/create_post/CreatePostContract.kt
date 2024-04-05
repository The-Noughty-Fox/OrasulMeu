package com.thenoughtfox.orasulmeu.ui.create_post

import android.net.Uri

data class State(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val images: List<Uri> = listOf(),
    val title: String = "",
    val description: String = "",
    val image: Uri? = null
)

sealed class Event {
    data object GoToPostPage : Event()
    data object BackToMediaPage : Event()
    data object OnClickMedia : Event()
    data object OnClickCamera : Event()
    data class SetTitle(val title: String) : Event()
    data class SetDescription(val desc: String) : Event()
    data object Submit : Event()
    data class PickImages(val uris: List<Uri>) : Event()
    data class SelectImage(val image:Uri) : Event()
}

sealed class Action {
    data object OpenPhotoPicker : Action()
    data object OpenCamera : Action()
    data class ShowToast(val msg: String) : Action()
}
