package com.thenoughtfox.orasulmeu.ui.screens.profile

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.thenoughtfox.orasulmeu.net.helper.toOperationResult
import com.thenoughtfox.orasulmeu.service.UserSharedPrefs
import com.thenoughtfox.orasulmeu.ui.screens.home.utils.CombinedPostsPagingSource
import com.thenoughtfox.orasulmeu.ui.screens.home.utils.PostType
import com.thenoughtfox.orasulmeu.ui.screens.profile.ProfileContract.Action
import com.thenoughtfox.orasulmeu.ui.screens.profile.ProfileContract.Event
import com.thenoughtfox.orasulmeu.ui.screens.profile.ProfileContract.State
import com.thenoughtfox.orasulmeu.utils.MimeType
import com.thenoughtfox.orasulmeu.utils.UploadUtils.toMultiPart
import com.thenoughtfox.orasulmeu.utils.getRealPathFromURI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.openapitools.client.apis.MediaApi
import org.openapitools.client.apis.PostsApi
import org.openapitools.client.apis.UsersApi
import org.openapitools.client.models.MediaSupabaseDto
import org.openapitools.client.models.PostDto
import org.openapitools.client.models.UserDto
import org.openapitools.client.models.UserUpdateDto
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userSharedPrefs: UserSharedPrefs,
    private val usersApi: UsersApi,
    private val mediaApi: MediaApi,
    private val postsApi: PostsApi,
    private val application: Application
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private val _action = MutableSharedFlow<Action>()
    val action: SharedFlow<Action> = _action

    val event = Channel<Event>(Channel.UNLIMITED)

    init {
        handleEvents()
        getMyPosts()
        userSharedPrefs.user?.let { user ->
            _state.update {
                it.copy(
                    isLoading = false,
                    name = user.userName ?: "empty name",
                    imageUrl = user.socialProfilePictureUrl,
                )
            }
        }
    }

    private fun handleEvents() = viewModelScope.launch {
        event.consumeAsFlow().collect { event ->
            when (event) {
                Event.EditProfile -> {
                    _state.update { it.copy(isEditing = true) }
                }

                is Event.ChangeName -> {
                    _state.update { it.copy(newName = event.newName, name = event.newName) }
                }

                is Event.ChangePicture -> {
                    _state.update {
                        it.copy(
                            newImageUri = event.image,
                            imageUrl = event.image.toString()
                        )
                    }
                }

                Event.SaveChanges -> {
                    updateUser()
                }

                Event.DiscardChanges -> {
                    _state.update {
                        it.copy(
                            isEditing = false,
                            name = userSharedPrefs.user?.userName ?: "empty name",
                            imageUrl = userSharedPrefs.user?.socialProfilePictureUrl
                        )
                    }
                }
            }
        }
    }

    private fun updateUser() = viewModelScope.launch {
        val updateUserNameDeferred = async {
            if (state.value.newName == null) {
                return@async null
            }

            usersApi.editUser(UserUpdateDto(username = state.value.name)).toOperationResult { it }
        }

        val updateUserPhotoDeferred = async {
            val imageUri = state.value.newImageUri ?: return@async null

            val path = getRealPathFromURI(
                contentUri = imageUri,
                context = application.applicationContext
            )

            val FILE_FORM_DATA = "file"
            val part = if (path.isNullOrEmpty()) {
                toMultiPart(imageUri, FILE_FORM_DATA, MimeType.IMAGE.mimeTypes.first())
            } else {
                toMultiPart(path, FILE_FORM_DATA, MimeType.IMAGE.mimeTypes.first())
            }

            mediaApi.upload(part).toOperationResult { it }
        }

        val results = awaitAll(updateUserNameDeferred, updateUserPhotoDeferred)

        val updateUserNameResult = results[0]
        val updateUserPhotoResult = results[1]

        _state.update { it.copy(isEditing = false) }
        updateUserNameResult?.onSuccess { user ->
            val username = (user as? UserDto)?.username
            userSharedPrefs.user = userSharedPrefs.user?.copy(userName = username)
        }?.onError {
            _action.emit(Action.ShowToast("Failed to update user name"))
            _state.update {
                it.copy(
                    isEditing = false,
                    name = userSharedPrefs.user?.userName ?: "empty name",
                )
            }
        }

        updateUserPhotoResult?.onSuccess { media ->
            val url = (media as? MediaSupabaseDto)?.url
            userSharedPrefs.user = userSharedPrefs.user?.copy(socialProfilePictureUrl = url)
        }?.onError {
            _action.emit(Action.ShowToast("Failed to update user photo"))
            _state.update {
                it.copy(
                    isEditing = false,
                    imageUrl = userSharedPrefs.user?.socialProfilePictureUrl
                )
            }
        }
    }

    private fun getMyPosts() = viewModelScope.launch {
        val myPosts: Flow<PagingData<PostDto>> = Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CombinedPostsPagingSource(postsApi).getPostsPagingSource(type = PostType.MY)
            }
        ).flow.cachedIn(viewModelScope)

        _state.update { it.copy(myPosts = myPosts) }
    }
}