package com.thenoughtfox.orasulmeu.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat

fun Context.getCompatDrawable(drawable: Int): Drawable? =
    ContextCompat.getDrawable(this, drawable)

fun Context.showToast(msg: String?, duration: Int? = null) {
    Toast.makeText(this, msg, duration ?: Toast.LENGTH_SHORT).show()
}

fun Context.getCompatColor(color: Int): Int = ContextCompat.getColor(this, color)
fun Context.getDimen(dimenId: Int): Float = resources.getDimension(dimenId)
fun Context.getDrawableCompat(drawableId: Int): Drawable? =
    ContextCompat.getDrawable(this, drawableId)

fun Context.showKeyboard() {
    val inputMethodManager: InputMethodManager =
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}