package com.thenoughtfox.orasulmeu.ui.post.utils

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: Int? = null,
    val title: String = "",
    val content: String = "",
    val media: List<String> = listOf(),
    val locationAddress: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)