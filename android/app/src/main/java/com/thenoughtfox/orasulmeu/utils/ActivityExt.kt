package com.thenoughtfox.orasulmeu.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.Window
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.thenoughtfox.orasulmeu.R

fun Activity.setTransparentStatusBar(isLightTheme: Boolean = true) = window?.apply {
    WindowCompat.setDecorFitsSystemWindows(this, false)
    statusBarColor = getCompatColor(android.R.color.transparent)
    setWindowLightStatusBar(isLightTheme)
}

fun Activity.showStatusBar(statusColor: Int? = null) = window?.apply {
    WindowCompat.setDecorFitsSystemWindows(this, true)
    statusBarColor = getCompatColor(statusColor ?: R.color.background_color)
}

fun Window.setWindowLightStatusBar(isLightTheme: Boolean) {
    ViewCompat.getWindowInsetsController(this.decorView)?.apply {
        isAppearanceLightStatusBars = isLightTheme
        show(WindowInsetsCompat.Type.statusBars())
    }
}

/**
 * Navigate to system settings page.
 */
fun Activity.navigateToSettings(isNeedToEnablePermission: Boolean) {
    val intent = if (isNeedToEnablePermission) {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
    } else {
        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    }

    startActivity(intent)
}
