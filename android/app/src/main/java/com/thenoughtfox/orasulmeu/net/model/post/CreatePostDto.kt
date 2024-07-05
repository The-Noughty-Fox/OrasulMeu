package com.thenoughtfox.orasulmeu.net.model.post

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.thenoughtfox.orasulmeu.net.model.PointDto
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreatePostDto(
    @SerializedName("title")
    val title: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("locationAddress")
    val locationAddress: String,

    @SerializedName("location")
    val location: PointDto
) : Parcelable