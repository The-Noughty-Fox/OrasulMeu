package com.thenoughtfox.orasulmeu.utils

import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object UploadUtils {
    val String.toRequestBody: RequestBody
        get() = toRequestBody(MultipartBody.FORM)

    fun toMultiPart(path: Uri, formData: String, mimeType: String): MultipartBody.Part {
        val file = File(path.path ?: "")
        val reqBody = file.asRequestBody(mimeType.toMediaTypeOrNull())

        return MultipartBody.Part.createFormData(formData, file.name, reqBody)
    }

    fun toMultiPart(path: String, formData: String, mimeType: String): MultipartBody.Part {
        val file = File(path)
        val reqBody = file.asRequestBody(mimeType.toMediaTypeOrNull())

        return MultipartBody.Part.createFormData(formData, file.name, reqBody)
    }
}

enum class MimeType(val mimeTypes: List<String>) {
    IMAGE(listOf("image/jpeg", "image/bmp", "image/gif", "image/jpg", "image/png")),
    VIDEO(listOf("video/wav", "video/mp4")),
    TEXT(listOf("text/plain"))
}