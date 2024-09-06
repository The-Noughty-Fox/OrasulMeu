package com.thenoughtfox.orasulmeu.ui.screens.create_post

import android.app.Application
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import com.thenoughtfox.orasulmeu.navigation.RootNavDestinations
import com.thenoughtfox.orasulmeu.net.helper.toOperationResult
import com.thenoughtfox.orasulmeu.ui.post.utils.IMAGE
import com.thenoughtfox.orasulmeu.ui.post.utils.Media
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostContract.Action
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostContract.Event
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostContract.State
import com.thenoughtfox.orasulmeu.utils.MimeType
import com.thenoughtfox.orasulmeu.utils.UploadUtils.toMultiPart
import com.thenoughtfox.orasulmeu.utils.getRealPathFromURI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.openapitools.client.apis.MediaApi
import org.openapitools.client.apis.PostsApi
import org.openapitools.client.models.CreatePostDto
import org.openapitools.client.models.MediaSupabaseDto
import org.openapitools.client.models.PointDto
import org.openapitools.client.models.PostDto
import org.openapitools.client.models.UpdatePostDto
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val postsApi: PostsApi,
    private val mediaApi: MediaApi,
    private val application: Application,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val event = Channel<Event>(Channel.UNLIMITED)

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private val _action = MutableSharedFlow<Action>()
    val action: SharedFlow<Action> = _action

    private val navDestinationPost = RootNavDestinations.CreatePost.from(savedStateHandle)

    init {
        handleEvents()

        // Get post data if it's an edit
        getPostData()
    }

    private fun handleEvents() = viewModelScope.launch {
        event.consumeAsFlow().collect { event ->
            when (event) {
                is Event.SetTitle -> _state.update { it.copy(title = event.title) }
                is Event.SetContent -> _state.update { it.copy(content = event.content) }
                Event.Submit -> {
                    if (state.value.isEdit) {
                        updatePost()
                    } else {
                        sendPost()
                    }
                }

                is Event.PickImages -> addImages(event.uris)
                is Event.SelectImage -> _state.update { it.copy(image = event.image) }
                is Event.SetAddress -> {
                    _state.update { it.copy(address = event.address, currentPoint = event.point) }
                }

                is Event.RemoveImage -> removeImage(event.image)
                is Event.ShowAlert -> _state.update { it.copy(removedImage = event.image) }
                Event.DismissAlert -> _state.update { it.copy(removedImage = null) }
            }
        }
    }

    private fun getPostData() {
        val post = navDestinationPost.post
        if (navDestinationPost.post.id != null) {
            val postMedia = navDestinationPost.post.media
            _state.update {
                it.copy(
                    title = post.title,
                    content = post.content,
                    address = post.locationAddress,
                    currentPoint = Point.fromLngLat(
                        post.longitude, post.latitude
                    ),
                    images = postMedia.map { media ->
                        Image(media = media, isUri = false)
                    },
                    image = Image(media = postMedia.first(), isUri = false),
                    isEdit = true
                )
            }
        }
    }

    private fun addImages(images: List<Uri>) {
        val allImages = state.value.images + images.map {
            Image(Media(url = it.toString()))
        }

        val image = allImages.first()
        _state.update {
            it.copy(images = allImages.distinct(), image = image)
        }
    }

    private fun removeImage(image: Image) {
        val removedImages = state.value.removedImages + listOf(image)
        val images = state.value.images.toMutableList()
        images.remove(image)
        _state.update {
            it.copy(
                images = images,
                image = if (images.isNotEmpty()) images.first() else null,
                removedImage = null,
                removedImages = removedImages
            )
        }
    }

    private fun sendPost() = viewModelScope.launch(Dispatchers.IO) {
        _state.update { it.copy(isLoading = true) }
        val post = CreatePostDto(
            title = state.value.title,
            content = state.value.content,
            locationAddress = state.value.address,
            location = if (state.value.currentPoint == null) {
                null
            } else {
                PointDto(
                    latitude = state.value.currentPoint!!.latitude(),
                    longitude = state.value.currentPoint!!.longitude()
                )
            }
        )

        postsApi.createPost(post)
            .toOperationResult { it }
            .onSuccess { postDto -> sendPostMedia(postDto) }
            .onError {
                _state.update { it.copy(isLoading = false) }
                _action.emit(Action.ShowToast(it))
            }
    }

    private fun updatePost() = viewModelScope.launch {
        val id = navDestinationPost.post.id ?: return@launch
        _state.update { it.copy(isLoading = true) }
        postsApi.updatePost(
            id = id, updatePostDto = UpdatePostDto(
                title = state.value.title,
                content = state.value.content,
                locationAddress = state.value.address,
                location = if (state.value.currentPoint == null) {
                    null
                } else {
                    PointDto(
                        latitude = state.value.currentPoint!!.latitude(),
                        longitude = state.value.currentPoint!!.longitude()
                    )
                }
            )
        )
            .toOperationResult { it }
            .onSuccess { post ->
                val areAllImagesUploaded =
                    state.value.images.none { it.isUri }
                            && state.value.removedImages.filterNot { it.isUri }.isEmpty()

                if (areAllImagesUploaded) {
                    _state.update { it.copy(isLoading = false) }
                    _action.emit(Action.GoBackToProfile(post))
                } else {
                    deleteImages(post)
                }
            }
            .onError { error ->
                _state.update { it.copy(isLoading = false) }
                _action.emit(Action.ShowToast(error))
            }
    }

    private fun deleteImages(post: PostDto) = viewModelScope.launch {
        val removedImages = state.value.removedImages.filterNot { it.isUri }
        if (removedImages.isNotEmpty()) {
            val deletionTasks = removedImages.map { image ->
                async {
                    val media = image.media
                    val type = if (media.type == IMAGE) {
                        MediaSupabaseDto.Type.image
                    } else {
                        MediaSupabaseDto.Type.video
                    }

                    mediaApi.deleteMedia(
                        MediaSupabaseDto(
                            id = media.id, type = type, url = media.url,
                            bucketPath = media.bucketPath, fileName = media.fileName
                        )
                    ).toOperationResult { it }
                }
            }

            try {
                deletionTasks.awaitAll().forEach { result ->
                    result.onError { error ->
                        _state.update { it.copy(isLoading = false, isError = true) }
                        _action.emit(Action.ShowToast(error))
                        return@onError
                    }
                }

                if (state.value.images.none { it.isUri }) {
                    _action.emit(Action.GoBackToProfile(post))
                } else {
                    sendPostMedia(post)
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, isError = true) }
                _action.emit(Action.ShowToast(e.message ?: "Unknown error"))
            }
        } else {
            if (state.value.images.none { it.isUri }) {
                _action.emit(Action.GoBackToProfile(post))
            } else {
                sendPostMedia(post)
            }
        }
    }

    private suspend fun sendPostMedia(postDto: PostDto) {
        val parts = state.value.images.mapNotNull { image ->
            if (image.isUri) {
                val uri = Uri.parse(image.media.url)
                val FILES_FORM_DATA = "files"
                val path =
                    getRealPathFromURI(contentUri = uri, context = application.applicationContext)

                if (path.isNullOrEmpty()) {
                    toMultiPart(uri, FILES_FORM_DATA, MimeType.IMAGE.mimeTypes.first())
                } else {
                    toMultiPart(path, FILES_FORM_DATA, MimeType.IMAGE.mimeTypes.first())
                }
            } else {
                null
            }
        }

        postsApi.uploadPostMedia(id = postDto.id, parts)
            .toOperationResult { it }
            .onSuccess {
                _state.update { it.copy(isLoading = false) }
                if (state.value.isEdit) {
                    _action.emit(Action.GoBackToProfile(postDto))
                } else {
                    _action.emit(Action.GoMain)
                }
            }
            .onError { error ->
                _state.update { it.copy(isLoading = false, isError = true) }
                _action.emit(Action.ShowToast(error))
            }
    }
}