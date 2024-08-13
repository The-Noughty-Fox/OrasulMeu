package com.thenoughtfox.orasulmeu.ui.screens.create_post

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thenoughtfox.orasulmeu.net.helper.toOperationResult
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostContract.Action
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostContract.Event
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostContract.State
import com.thenoughtfox.orasulmeu.utils.MimeType
import com.thenoughtfox.orasulmeu.utils.UploadUtils.toMultiPart
import com.thenoughtfox.orasulmeu.utils.getRealPathFromURI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.openapitools.client.apis.PostsApi
import org.openapitools.client.models.CreatePostDto
import org.openapitools.client.models.PointDto
import javax.inject.Inject

private const val FILES_FORM_DATA = "files"

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val postsApi: PostsApi,
    private val application: Application,
) : ViewModel() {

    val event = Channel<Event>(Channel.UNLIMITED)

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private val _action = MutableSharedFlow<Action>()
    val action: SharedFlow<Action> = _action

    init {
        handleEvents()
    }

    private fun handleEvents() = viewModelScope.launch {
        event.consumeAsFlow().collect { event ->
            when (event) {
                is Event.SetTitle -> _state.update { it.copy(title = event.title) }
                is Event.SetDescription -> _state.update { it.copy(description = event.desc) }
                Event.Submit -> sendPost()
                is Event.PickImages -> addImages(event.uris)
                is Event.SelectImage -> _state.update { it.copy(image = event.image) }
                is Event.SetAddress -> {
                    _state.update { it.copy(address = event.address, currentPoint = event.point) }
                }

                is Event.RemoveImage -> removeImage(event.image)
                is Event.ShowAlert -> _state.update { it.copy(removedUri = event.uri) }
                Event.DismissAlert -> _state.update { it.copy(removedUri = null) }
            }
        }
    }

    private fun addImages(images: List<Uri>) {
        val allImages = state.value.images + images
        _state.update {
            it.copy(images = allImages.distinct(), image = allImages.first())
        }
    }

    private fun removeImage(image: Uri) {
        val images = state.value.images.toMutableList()
        images.remove(image)
        _state.update {
            it.copy(
                images = images,
                image = if (images.isNotEmpty()) images.first() else null,
                removedUri = null
            )
        }
    }

    private fun sendPost() = viewModelScope.launch(Dispatchers.IO) {
        _state.update { it.copy(isLoading = true) }
        val post = CreatePostDto(
            title = state.value.title,
            content = state.value.description,
            locationAddress = state.value.address,
            location = PointDto(
                latitude = state.value.currentPoint.latitude(),
                longitude = state.value.currentPoint.longitude()
            ) // @TODO @lsimonenco This is required param ???
        )

        postsApi.createPost(post)
            .toOperationResult { it }
            .onSuccess { postDto -> sendPostMedia(postDto.id) }
            .onError {
                _state.update { it.copy(isLoading = false) }
                _action.emit(Action.ShowToast(it))
            }
    }

    private suspend fun sendPostMedia(id: Int) {
        val parts = state.value.images.map { uri ->
            val path =
                getRealPathFromURI(contentUri = uri, context = application.applicationContext)

            if (path.isNullOrEmpty()) {
                toMultiPart(uri, FILES_FORM_DATA, MimeType.IMAGE.mimeTypes.first())
            } else {
                toMultiPart(path, FILES_FORM_DATA, MimeType.IMAGE.mimeTypes.first())
            }
        }

        postsApi.uploadPostMedia(id = id, parts)
            .toOperationResult { it }
            .onSuccess {
                _state.update { it.copy(isLoading = false) }
                _action.emit(Action.GoBack)
            }
            .onError {
                _state.update { it.copy(isLoading = false) }
                _action.emit(Action.ShowToast(it))
            }
    }
}