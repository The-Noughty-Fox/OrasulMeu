package com.thenoughtfox.orasulmeu.ui.create_post.camera.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.ExecutorService
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class PerformImageCaptureUseCase(
    private val context: Context,
    private val imageCapture: ImageCapture,
    private val cameraExecutor: ExecutorService,
) {
    suspend operator fun invoke() = suspendCoroutine { continuation ->
        imageCapture.takePicture(cameraExecutor, object :
            OnImageCapturedCallback() {
            @OptIn(ExperimentalGetImage::class)
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                val bmp = image.toBitmap().rotate(image.imageInfo.rotationDegrees.toFloat())

                val externalFileDir =
                    context.getExternalFilesDir(Environment.DIRECTORY_DCIM).toString()
                val fileName = externalFileDir + "/" + System.currentTimeMillis() + ".jpeg"

                val imageFile: File? = try {
                    FileOutputStream(fileName).use { out ->
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    }

                    File(fileName)
                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                }

                imageFile?.let {
                    continuation.resume(Uri.fromFile(it))
                } ?: {
                    continuation.resume(null)
                }
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                continuation.resume(null)
            }
        })
    }

    private fun Bitmap.rotate(degrees: Float): Bitmap =
        Bitmap.createBitmap(this, 0, 0, width, height, Matrix().apply { postRotate(degrees) }, true)
}