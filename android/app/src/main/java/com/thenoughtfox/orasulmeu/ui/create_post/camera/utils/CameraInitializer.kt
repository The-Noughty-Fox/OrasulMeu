package com.thenoughtfox.orasulmeu.ui.create_post.camera.utils

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import timber.log.Timber

class CameraInitializer (
    private val context: Context,
    private val imageCapture: ImageCapture
) {
    fun initCamera(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener(
            { initCameraProvider(previewView, lifecycleOwner, cameraProviderFuture) },
            ContextCompat.getMainExecutor(context)
        )
    }

    private fun initCameraProvider(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    ) {
        // Used to bind the lifecycle of cameras to the lifecycle owner
        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

        // Preview
        val imagePreview = PreviewFactory.create(previewView)
        imagePreview.setSurfaceProvider(previewView.surfaceProvider)

        // Select back camera as a default
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            // Unbind use cases before rebinding
            cameraProvider.unbindAll()

            previewView.viewPort?.let { viewport ->
                val useCaseGroup =
                    UseCaseGroup.Builder()
                        .addUseCase(imageCapture)
                        .addUseCase(imagePreview)
                        .setViewPort(viewport)
                        .build()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    useCaseGroup
                )
            }

        } catch (e: Exception) {
            Timber.tag("check").e(e, "Use case binding failed")
        }
    }
}