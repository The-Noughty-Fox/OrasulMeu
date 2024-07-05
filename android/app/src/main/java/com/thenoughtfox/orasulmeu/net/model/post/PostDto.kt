
package com.thenoughtfox.orasulmeu.net.model.post

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.thenoughtfox.orasulmeu.net.model.PointDto
import com.thenoughtfox.orasulmeu.net.model.user.UserDto
import com.thenoughtfox.orasulmeu.net.model.media.Media
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostDto (

    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("author")
    val author: UserDto,

    @SerializedName("reactions")
    val reactions: PostReactionsDto,

    @SerializedName("media")
    val media: List<Media>,

    @SerializedName("locationAddress")
    val locationAddress: String,

    @SerializedName("location")
    val location: PointDto

) : Parcelable

