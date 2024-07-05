package com.thenoughtfox.orasulmeu.net.model.media

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Media (

    @SerializedName("id")
    val id: Int,

    @SerializedName("type")
    val type: Type,

    @SerializedName("url")
    val url: kotlin.String,

    @SerializedName("fileName")
    val fileName: kotlin.String

) : Parcelable {

    /**
     * 
     *
     * Values: image,video
     */
    enum class Type(val value: kotlin.String) {
        @SerializedName(value = "image") image("image"),
        @SerializedName(value = "video") video("video");
    }
}

