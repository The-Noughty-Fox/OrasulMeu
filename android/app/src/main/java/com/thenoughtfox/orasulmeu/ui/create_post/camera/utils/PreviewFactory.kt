package com.thenoughtfox.orasulmeu.ui.create_post.camera.utils

import androidx.camera.core.Preview
import androidx.camera.view.PreviewView

object PreviewFactory {
    fun create(previewView: PreviewView): Preview =
        Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
}