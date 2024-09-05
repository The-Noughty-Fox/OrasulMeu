package com.thenoughtfox.orasulmeu.ui.post.utils

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: Int? = null,
    val title: String = "",
    val content: String = "",
    val locationAddress: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val media: List<Media> = emptyList(),
)

@Serializable
data class Media(
    val id: Int = 0,
    val type: String = IMAGE, /*image or video*/
    val url: String = "",
    val bucketPath: String = "",
    val fileName: String = ""
)

const val IMAGE = "image"
const val VIDEO = "video"