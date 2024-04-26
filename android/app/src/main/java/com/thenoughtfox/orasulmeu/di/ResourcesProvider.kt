package com.thenoughtfox.orasulmeu.di

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.thenoughtfox.orasulmeu.utils.getCompatColor
import com.thenoughtfox.orasulmeu.utils.getCompatDrawable
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class ResourcesProvider @Inject constructor(@ApplicationContext private val context: Context) {
    fun getString(@StringRes stringResId: Int): String = context.getString(stringResId)
    fun getStringArray(stringResId: Int): Array<out String> =
        context.resources.getStringArray(stringResId)

    fun getDrawable(@DrawableRes drawableResId: Int): Drawable? =
        context.getCompatDrawable(drawableResId)

    fun getColor(@ColorInt colorResId: Int): Int = context.getCompatColor(colorResId)
}