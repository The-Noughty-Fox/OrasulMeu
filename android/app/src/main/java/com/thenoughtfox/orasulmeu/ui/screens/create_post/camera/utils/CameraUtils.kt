package com.thenoughtfox.orasulmeu.ui.screens.create_post.camera.utils

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
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object CameraUtils {

    suspend fun Context.getCameraProvider(): ProcessCameraProvider =
        suspendCoroutine { continuation ->
            ProcessCameraProvider.getInstance(this).also { cameraProvider ->
                cameraProvider.addListener({
                    continuation.resume(cameraProvider.get())
                }, ContextCompat.getMainExecutor(this))
            }
        }

    suspend fun takePicture(imageCapture: ImageCapture, context: Context) =
        suspendCoroutine { continuation ->
            val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
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