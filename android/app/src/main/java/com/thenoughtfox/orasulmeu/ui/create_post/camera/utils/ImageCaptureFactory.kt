package com.thenoughtfox.orasulmeu.ui.create_post.camera.utils

import androidx.camera.core.ImageCapture

object ImageCaptureFactory {
    fun create() = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .build()
}