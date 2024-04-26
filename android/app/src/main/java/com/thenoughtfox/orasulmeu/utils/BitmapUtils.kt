package com.thenoughtfox.orasulmeu.utils

import android.content.Context
import android.graphics.Bitmap

fun Bitmap.generateSmallIcon(context: Context, sizeWidth: Int, sizeHeight: Int): Bitmap {
    val height = sizeHeight.dp(context)
    val width = sizeWidth.dp(context)
    return Bitmap.createScaledBitmap(this, width, height, false)
}