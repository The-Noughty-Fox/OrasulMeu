package com.thenoughtfox.orasulmeu.ui.screens.create_post.camera

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.databinding.FragmentCameraBinding
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostContract
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostViewModel
import com.thenoughtfox.orasulmeu.ui.screens.create_post.camera.utils.CameraInitializer
import com.thenoughtfox.orasulmeu.ui.screens.create_post.camera.utils.ImageCaptureFactory
import com.thenoughtfox.orasulmeu.ui.screens.create_post.camera.utils.PerformImageCaptureUseCase
import com.thenoughtfox.orasulmeu.utils.applyBottomInsetMargin
import com.thenoughtfox.orasulmeu.utils.showToast
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

/**
 * @author Knurenko Bogdan 07.06.2024
 */

@Composable
fun CameraController(viewModel: CreatePostViewModel) {
    val context = LocalContext.current
    val lifeCycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()


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

    val imageCapture = remember { ImageCaptureFactory.create() }
    val cameraInitializer = remember {
        CameraInitializer(
            context = context,
            imageCapture = imageCapture
        )
    }

    var isCameraPermissionGranted: Boolean by remember { mutableStateOf(false) }
    val cameraRequester = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isCameraPermissionGranted = true
        } else {
            context.showToast("Please provide location permissions")
            askUserToOpenSettings(context)
        }
    }

    val performImageCaptureUseCase = remember {
        PerformImageCaptureUseCase(
            context = context,
            imageCapture = imageCapture,
            cameraExecutor = Executors.newSingleThreadExecutor()
        )
    }

    LaunchedEffect(Unit) {
        cameraRequester.launch(Manifest.permission.CAMERA)
    }

    AndroidViewBinding(
        factory = { inflater, parent, attach ->
            FragmentCameraBinding.inflate(inflater, parent, attach).apply {
                buttonLayout.applyBottomInsetMargin()
                imageViewGallery.setOnClickListener {
                    pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }

                imageViewNext.setOnClickListener {
                    scope.launch {
                        viewModel.event.send(CreatePostContract.Event.BackToMediaPage)
                    }
                }

                imageViewShutter.setOnClickListener {
                    scope.launch {
                        performImageCaptureUseCase()?.let { successUri ->
                            viewModel.event.send(
                                CreatePostContract.Event.PickImages(
                                    listOf(
                                        successUri
                                    )
                                )
                            )
                            viewModel.event.send(CreatePostContract.Event.BackToMediaPage)
                        }
                    }
                }
            }
        }
    ) {
        if (isCameraPermissionGranted) {
            cameraInitializer.initCamera(
                previewView = this.cameraPreview,
                lifecycleOwner = lifeCycleOwner
            )
        }
    }
}

private fun askUserToOpenSettings(context: Context) {
    AlertDialog.Builder(context).apply {
        setTitle(R.string.camera_permission_denied_alert_title)
        setMessage(R.string.camera_permission_denied_aler_message)
        setPositiveButton(R.string.camera_permission_denied_go_to_settings_button_text) { _, _ -> startAppSettings(context) }
        setNegativeButton(R.string.camera_permission_denied_alert_cancel_button) { dialog, _ -> dialog.dismiss() }
        create()
        show()
    }
}

private fun startAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri: Uri = Uri.fromParts("package", context.packageName, null)
    intent.data = uri
    context.startActivity(intent)
}