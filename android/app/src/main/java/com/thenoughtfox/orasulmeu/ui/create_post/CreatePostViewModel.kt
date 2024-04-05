package com.thenoughtfox.orasulmeu.ui.create_post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import com.thenoughtfox.orasulmeu.navigation.Screens.cameraScreen
import com.thenoughtfox.orasulmeu.navigation.Screens.createPostScreen
import com.thenoughtfox.orasulmeu.navigation.Screens.mediaPostScreen
import com.thenoughtfox.orasulmeu.net.helper.toOperationResult
import com.thenoughtfox.orasulmeu.utils.MimeType
import com.thenoughtfox.orasulmeu.utils.UploadUtils.toMultiPart
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
    private val postsApi: PostsApi
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
                Event.GoToPostPage -> router.navigateTo(createPostScreen())
                Event.BackToMediaPage -> router.backTo(mediaPostScreen())
                Event.OnClickCamera -> router.navigateTo(cameraScreen())
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
            toMultiPart(uri, "files", MimeType.IMAGE.mimeTypes.first())
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