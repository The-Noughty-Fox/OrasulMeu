package com.thenoughtfox.orasulmeu.ui.screens.create_post.map.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap

object PinUtils {

    private const val INSET: Int = 4

    fun maskDrawableToAnother(context: Context, sourceResId: Int, maskResId: Int): Bitmap {
        // Get the drawables
        val sourceDrawable = ContextCompat.getDrawable(context, sourceResId)
        val maskDrawable = ContextCompat.getDrawable(context, maskResId)

        // Convert the drawables to bitmaps
        val sourceBitmap = sourceDrawable?.toBitmap()
        val maskBitmap = maskDrawable?.toBitmap()

        if (sourceBitmap == null || maskBitmap == null) {
            return Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888)
        }

        val roundBitmap = getClip(sourceBitmap, maskBitmap)

        val result =
            Bitmap.createBitmap(maskBitmap.width, maskBitmap.height, Bitmap.Config.ARGB_8888)

        // Create a canvas to draw onto the result bitmap.
        val canvas = Canvas(result)

        // Draw the source bitmap onto the canvas.
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        canvas.drawBitmap(maskBitmap, 0f, 0f, paint)
        canvas.drawBitmap(roundBitmap, INSET.toFloat(), INSET.toFloat(), paint)

        return result
    }


    private fun getClip(bitmap: Bitmap, maskBitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(
            maskBitmap.getWidth() - INSET * 2,
            maskBitmap.getWidth() - INSET * 2, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.getWidth(), bitmap.getHeight())
        val destRect = Rect(0, 0, output.getWidth(), output.getWidth())
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(
            output.getWidth() / 2f, output.getHeight() / 2f,
            output.getWidth() / 2f, paint
        )

        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        canvas.drawBitmap(bitmap, rect, destRect, paint)
        return output
    }
}