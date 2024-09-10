package com.thenoughtfox.orasulmeu.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.drawable.Drawable
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.ComponentActivity
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

fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

internal fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Permissions should be called in the context of an Activity")
}