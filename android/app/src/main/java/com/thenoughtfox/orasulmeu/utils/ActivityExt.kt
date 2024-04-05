package com.thenoughtfox.orasulmeu.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.view.Window
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.thenoughtfox.orasulmeu.R
import com.thenoughtfox.orasulmeu.ui.create_post.Event
import kotlinx.coroutines.launch


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

fun Vibrator.vibrate() {
// Vibrate for 500 milliseconds
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        //deprecated in API 26
        vibrate(100)
    }
}


fun Context.getVibrator(): Vibrator {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =
            getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
}