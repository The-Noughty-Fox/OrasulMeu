package com.thenoughtfox.orasulmeu.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.Companion.isPhotoPickerAvailable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.thenoughtfox.orasulmeu.navigation.LocalProfileNavigator
import com.thenoughtfox.orasulmeu.navigation.ProfileDestinations
import com.thenoughtfox.orasulmeu.utils.showToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ProfileController() {

    val viewModel: ProfileViewModel = hiltViewModel()
    val profileNavController = LocalProfileNavigator.current
    val scope = rememberCoroutineScope()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current

    val pickImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri == null) return@rememberLauncherForActivityResult

            scope.launch {
                viewModel.event.send(ProfileContract.Event.ChangePicture(uri))
            }
        }
    )

    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            viewModel.action.collectLatest { action ->
                when (action) {
                    is ProfileContract.Action.ShowToast -> context.showToast(action.msg)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        scope.launch {
            viewModel.event.send(ProfileContract.Event.LoadProfile)
        }
    }

    ProfileScreen(
        posts = viewModel.myPosts.collectAsLazyPagingItems(),
        state = viewModel.state.collectAsState().value,
        sendEvent = { event ->
            scope.launch {
                viewModel.event.send(event)
            }
        },
        pickImage = {
            if (isPhotoPickerAvailable(context)) {
                pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                PictureSelector.create(context)
                    .openSystemGallery(SelectMimeType.ofImage())
                    .setSelectionMode(SelectModeConfig.SINGLE)
                    .forSystemResult(object : OnResultCallbackListener<LocalMedia> {
                        override fun onResult(result: ArrayList<LocalMedia>) {
                            scope.launch {
                                val file = result.first()
                                val uri = Uri.fromFile(File(file.realPath))
                                viewModel.event.send(ProfileContract.Event.ChangePicture(uri))
                            }
                        }

                        override fun onCancel() {
                        }
                    })
            }
        },
        sendNavEvent = { profileNavController.navigate(ProfileDestinations.ProfileSettings) }
    )
}