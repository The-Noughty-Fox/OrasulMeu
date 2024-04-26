package com.thenoughtfox.orasulmeu.ui.create_post

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import com.thenoughtfox.orasulmeu.navigation.Screens
import com.thenoughtfox.orasulmeu.net.helper.toOperationResult
import com.thenoughtfox.orasulmeu.ui.create_post.CreatePostContract.Action
import com.thenoughtfox.orasulmeu.ui.create_post.CreatePostContract.Event
import com.thenoughtfox.orasulmeu.ui.create_post.CreatePostContract.State
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
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val router: Router,
    private val postsApi: PostsApi,
    private val application: Application
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
                Event.GoToPostPage -> router.navigateTo(Screens.createPostScreen)
                Event.BackToMediaPage -> router.backTo(Screens.mediaPostScreen)
                Event.OnClickCamera -> router.navigateTo(Screens.cameraScreen)
                Event.OnClickMedia -> _action.emit(Action.OpenPhotoPicker)
                is Event.SetTitle -> _state.update { it.copy(title = event.title) }
                is Event.SetDescription -> _state.update { it.copy(description = event.desc) }
                Event.Submit -> sendPost()
                is Event.PickImages -> {
                    val allImages = state.value.images + event.uris
                    _state.update {
                        it.copy(images = allImages.distinct(), image = allImages.first())
                    }
                }

                is Event.SelectImage -> _state.update { it.copy(image = event.image) }
                is Event.SetAddress -> _state.update { it.copy(address = event.address) }
                Event.GoToMapSearch -> router.navigateTo(Screens.mapSearchScreen)
            }
        }
    }

    private fun sendPost() = viewModelScope.launch(Dispatchers.IO) {
        _state.update { it.copy(isLoading = true) }
        val post = CreatePostDto(title = state.value.title, content = state.value.description)
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
            if (path != null) {
                toMultiPart(path, "files", MimeType.IMAGE.mimeTypes.first())
            } else {
                toMultiPart(uri, "files", MimeType.IMAGE.mimeTypes.first())
            }
        }

        postsApi.uploadPostMedia(id = id, parts)
            .toOperationResult { it }
            .onSuccess {
                _state.update { it.copy(isLoading = false) }
                _action.emit(Action.ShowToast("NICEE"))
            }
            .onError {
                _state.update { it.copy(isLoading = false) }
                _action.emit(Action.ShowToast(it))
            }
    }
}