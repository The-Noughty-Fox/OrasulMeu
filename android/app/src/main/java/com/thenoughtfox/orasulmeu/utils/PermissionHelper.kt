package com.thenoughtfox.orasulmeu.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.content.PermissionChecker
import com.thenoughtfox.orasulmeu.R

fun isLocationPermissionGranted(context: Context?): Boolean {
    if (context == null) {
        return false
    }

    val coarseLocation =
        PermissionChecker
            .checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

    val fineLocation =
        PermissionChecker
            .checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)

    return coarseLocation == PermissionChecker.PERMISSION_GRANTED &&
            fineLocation == PermissionChecker.PERMISSION_GRANTED
}

fun showSettingsDialog(activity: Activity) {
    AlertDialog.Builder(activity).apply {
        val title = activity.getString(R.string.app_name)
        setTitle(title)
        setMessage(R.string.settings_dialog_message)
        setPositiveButton(R.string.settings_dialog_positive_button_text) { _, _ ->
            startAppSettings(activity)
        }
        setNegativeButton(R.string.deny) { dialog, _ -> dialog.dismiss() }
    }.run {
        create()
        show()
    }
}

private fun startAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri: Uri = Uri.fromParts("package", context.packageName, null)
    intent.data = uri
    context.startActivity(intent)
}