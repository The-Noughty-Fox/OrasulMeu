package com.thenoughtfox.orasulmeu.ui.screens.profile

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.InvalidatingPagingSourceFactory
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.thenoughtfox.orasulmeu.net.helper.toOperationResult
import com.thenoughtfox.orasulmeu.service.UserSharedPrefs
import com.thenoughtfox.orasulmeu.ui.post.utils.Post
import com.thenoughtfox.orasulmeu.ui.post.utils.Media
import com.thenoughtfox.orasulmeu.ui.screens.home.HomeContract.PostListEvents
import com.thenoughtfox.orasulmeu.ui.screens.home.utils.CombinedPostsPagingSource
import com.thenoughtfox.orasulmeu.ui.screens.home.utils.PostType
import com.thenoughtfox.orasulmeu.ui.screens.profile.ProfileContract.Action
import com.thenoughtfox.orasulmeu.ui.screens.profile.ProfileContract.Event
import com.thenoughtfox.orasulmeu.ui.screens.profile.ProfileContract.State
import com.thenoughtfox.orasulmeu.utils.MimeType
import com.thenoughtfox.orasulmeu.utils.UploadUtils.toMultiPart
import com.thenoughtfox.orasulmeu.utils.getRealPathFromURI
import com.thenoughtfox.orasulmeu.utils.urlEncode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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

    private val postsInvalidatingSourceFactory = InvalidatingPagingSourceFactory {
        CombinedPostsPagingSource(postsApi).getPostsPagingSource(type = PostType.MY)
    }

    private val modificationEvents = MutableStateFlow<List<PostListEvents>>(emptyList())

    val myPosts = Pager(
        PagingConfig(pageSize = 20),
        pagingSourceFactory = postsInvalidatingSourceFactory
    ).flow
        .cachedIn(viewModelScope)
        .combine(modificationEvents) { pagingData, modifications ->
            modifications.fold(pagingData) { acc, event ->
                applyPostListEvents(acc, event)
            }
        }

    init {
        handleEvents()
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

                Event.EnterSettings -> postsInvalidatingSourceFactory.invalidate()

                Event.Refresh -> {
                    _state.update { it.copy(isRefreshing = true) }
                    postsInvalidatingSourceFactory.invalidate()
                    _state.update { it.copy(isRefreshing = false) }
                }

                Event.LoadProfile -> getUserProfile()
                is Event.DeletePost -> deletePost(event.postId)
                is Event.EditPost -> editPost(event.postId)

                is Event.RefreshEditedPost -> {
                    modificationEvents.value += PostListEvents.Edit(event.post)
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

    private fun getUserProfile() = viewModelScope.launch {
        val id = userSharedPrefs.user?.id
        if (id == null) {
            _action.emit(Action.ShowToast("Failed to load user profile"))
            return@launch
        }

        usersApi.getUserProfile(id)
            .toOperationResult { it }
            .onSuccess { userProfile ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        reactionsCount = userProfile.reactionsCount,
                        postsCount = userProfile.publicationsCount,
                        name = userProfile.username,
                        imageUrl = userProfile.socialProfilePictureUrl,
                    )
                }
            }
            .onError {
                _action.emit(Action.ShowToast("Failed to load user profile"))
            }
    }

    private fun deletePost(id: Int) = viewModelScope.launch {
        postsApi.deletePost(id)
            .toOperationResult { it }
            .onSuccess {
                modificationEvents.value += PostListEvents.Delete(id)
            }
            .onError { }
    }

    private fun editPost(id: Int) = viewModelScope.launch {
        postsApi.getPost(id)
            .toOperationResult { it }
            .onSuccess {
                val media = it.media.map { media ->
                    Media(
                        id = media.id,
                        type = media.type.value,
                        url = media.url.urlEncode(),
                        bucketPath = media.bucketPath.urlEncode(),
                        fileName = media.fileName
                    )
                }

                val post = Post(
                    id = it.id,
                    title = it.title,
                    content = it.content,
                    locationAddress = it.locationAddress,
                    latitude = it.location.latitude,
                    longitude = it.location.longitude,
                    media = media
                )

                _action.emit(Action.GoEditPost(post))
            }
            .onError {
                _action.emit(Action.ShowToast("Failed to load post"))
            }
    }

    private fun applyPostListEvents(
        paging: PagingData<PostDto>, events: PostListEvents
    ): PagingData<PostDto> {
        return when (events) {
            is PostListEvents.Delete -> {
                paging.filter { it.id != events.postId }
            }

            is PostListEvents.Edit -> {
                paging.map {
                    if (it.id == events.post.id) {
                        it.copy(
                            title = events.post.title,
                            content = events.post.content,
                            media = events.post.media,
                            reactions = it.reactions
                        )
                    } else {
                        it
                    }
                }
            }

            else -> paging
        }
    }
}