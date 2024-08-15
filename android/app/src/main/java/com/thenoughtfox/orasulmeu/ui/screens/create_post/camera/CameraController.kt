package com.thenoughtfox.orasulmeu.ui.screens.create_post.camera

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.Companion.isPhotoPickerAvailable
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.navigation.LocalCreatePostNavigator
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostContract
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostContract.Event
import com.thenoughtfox.orasulmeu.ui.screens.create_post.CreatePostViewModel
import com.thenoughtfox.orasulmeu.ui.screens.create_post.camera.utils.CameraUtils.getCameraProvider
import com.thenoughtfox.orasulmeu.ui.screens.create_post.camera.utils.CameraUtils.takePicture
import com.thenoughtfox.orasulmeu.utils.showToast
import com.thenoughtfox.orasulmeu.utils.view.Alert
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun CameraController(viewModel: CreatePostViewModel) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    val createPostNavigator = LocalCreatePostNavigator.current

    val pickMultipleMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            if (uris.isNotEmpty()) {
                scope.launch {
                    viewModel.event.send(Event.PickImages(uris))
                }
            }
        }
    )

    var isOpenSettingsAlertVisible: Boolean by remember { mutableStateOf(false) }
    var isCameraPermissionGranted: Boolean by remember { mutableStateOf(false) }

    val cameraRequester = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isCameraPermissionGranted = true
        } else {
            context.showToast("Please provide location permissions")
            isOpenSettingsAlertVisible = true
        }
    }

    LaunchedEffect(Unit) {
        cameraRequester.launch(Manifest.permission.CAMERA)
    }

    CameraPreviewScreen(lifecycleOwner, context, isCameraPermissionGranted,
        onGallery = {
            if (isPhotoPickerAvailable(context)) {
                pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                PictureSelector.create(context)
                    .openSystemGallery(SelectMimeType.ofImage())
                    .setSelectionMode(SelectModeConfig.MULTIPLE)
                    .forSystemResult(object : OnResultCallbackListener<LocalMedia> {
                        override fun onResult(result: ArrayList<LocalMedia>) {
                            val uris = result.map { Uri.fromFile(File(it.realPath)) }
                            if (uris.isNotEmpty()) {
                                scope.launch {
                                    viewModel.event.send(Event.PickImages(uris))
                                }
                            }
                        }

                        override fun onCancel() {
                        }
                    })
            }
        }, onCapture = { imageCapture ->
            scope.launch {
                takePicture(imageCapture, context)?.let { successUri ->
                    viewModel.event.send(Event.PickImages(listOf(successUri)))
                    createPostNavigator.navigateUp()
                }
            }
        }, onNext = {
            createPostNavigator.navigateUp()
        })

    if (isOpenSettingsAlertVisible) {
        Alert(
            onDismissRequest = { isOpenSettingsAlertVisible = false },
            dialogTitle = stringResource(R.string.camera_permission_denied_alert_title),
            dialogText = stringResource(R.string.camera_permission_denied_aler_message),
            confirmText = stringResource(R.string.camera_permission_denied_go_to_settings_button_text),
            onConfirmation = {
                startAppSettings(context)
            },
            dismissText = stringResource(R.string.camera_permission_denied_alert_cancel_button)
        )
    }
}

@Composable
fun CameraPreviewScreen(
    lifecycleOwner: LifecycleOwner,
    context: Context,
    isCameraPermissionGranted: Boolean,
    onGallery: () -> Unit,
    onCapture: (ImageCapture) -> Unit,
    onNext: () -> Unit
) {
    val previewView = remember {
        PreviewView(context)
    }

    val imageCapture = remember {
        ImageCapture.Builder().build()
    }

    LaunchedEffect(isCameraPermissionGranted) {
        val cameraProvider = context.getCameraProvider()
        val preview = Preview.Builder().build()
        val cameraxSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()


        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, preview, imageCapture)
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            val (gallery, shutter, next) = createRefs()
            Image(
                painter = painterResource(id = R.drawable.ic_thumbnail),
                contentDescription = "gallery",
                modifier = Modifier
                    .constrainAs(gallery) {
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                        top.linkTo(parent.top)
                    }
                    .padding(start = 16.dp)
                    .size(56.dp)
                    .clickable {
                        onGallery()
                    })

            Image(
                painter = painterResource(id = R.drawable.ic_shutter),
                contentDescription = "shutter",
                modifier = Modifier
                    .constrainAs(shutter) {
                        start.linkTo(gallery.start)
                        end.linkTo(next.end)
                        bottom.linkTo(parent.bottom)
                        top.linkTo(parent.top)
                    }
                    .size(72.dp)
                    .clickable {
                        onCapture(imageCapture)
                    })

            Image(
                painter = painterResource(id = R.drawable.ic_button_next),
                contentDescription = "next",
                modifier = Modifier
                    .constrainAs(next) {
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        top.linkTo(parent.top)
                    }
                    .padding(end = 16.dp)
                    .size(40.dp)
                    .clickable {
                        onNext()
                    })
        }
    }
}

private fun startAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri: Uri = Uri.fromParts("package", context.packageName, null)
    intent.data = uri
    context.startActivity(intent)
}