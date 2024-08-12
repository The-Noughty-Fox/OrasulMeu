package com.thenoughtfox.orasulmeu.ui.screens.create_post.media

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.thenoughtfox.orasulmeu.navigation.CreatePostDestinations
import com.thenoughtfox.orasulmeu.navigation.LocalCreatePostNavigator
import com.thenoughtfox.orasulmeu.navigation.LocalMainNavigator
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostContract
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostContract.NavEvent
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostViewModel
import com.thenoughtfox.orasulmeu.utils.showToast
import kotlinx.coroutines.launch

@Composable
fun CreatePostMediaController(viewModel: CreatePostViewModel) {

    val navigator = LocalCreatePostNavigator.current
    val mainNavigator = LocalMainNavigator.current

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val uiState by viewModel.state.collectAsState()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val pickMultipleMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            if (uris.isNotEmpty()) {
                scope.launch {
                    viewModel.event.send(CreatePostContract.Event.PickImages(uris))
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.action.collect { action ->
                when (action) {
                    is CreatePostContract.Action.ShowToast -> {
                        context.showToast(action.msg)
                    }
                }
            }
        }
    }

    CreatePostMediaPage(
        uiState = uiState,
        onSendEvent = { scope.launch { viewModel.event.send(it) } },
        onGalleryClick = {
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        sendNavEvent = { event ->
            when (event) {
                NavEvent.GoBack -> mainNavigator.navigateUp()
                NavEvent.Camera -> navigator.navigate(CreatePostDestinations.CameraScreen)
                NavEvent.CreatePost -> navigator.navigate(CreatePostDestinations.CreatePostScreen)
                else -> Unit
            }
        }
    )
}