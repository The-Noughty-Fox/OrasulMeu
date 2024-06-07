package com.thenoughtfox.orasulmeu.ui.screens.create_post.media

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostContract
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostViewModel
import com.thenoughtfox.orasulmeu.utils.showToast
import kotlinx.coroutines.launch

/**
 * @author Knurenko Bogdan 07.06.2024
 */

@Composable
fun CreatePostMediaController(viewModel: CreatePostViewModel) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val uiState = viewModel.state.collectAsState().value
    val action = viewModel.action.collectAsState(initial = CreatePostContract.Action.Initial).value

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

    LaunchedEffect(action) {
        when (action) {
            is CreatePostContract.Action.ShowToast -> {
                context.showToast(action.msg)
            }

            CreatePostContract.Action.OpenPhotoPicker -> {
                pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            CreatePostContract.Action.Initial -> Unit
        }
    }

    CreatePostMediaPage(
        uiState = uiState,
        onSendEvent = { scope.launch { viewModel.event.send(it) } }
    )
}