package com.thenoughtfox.orasulmeu.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
    var cursor: Cursor? = null
    return try {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        cursor = context.contentResolver.query(contentUri, proj, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA) ?: return ""
        cursor.moveToFirst()
        cursor.getString(columnIndex)
    } finally {
        cursor?.close()
    }
}