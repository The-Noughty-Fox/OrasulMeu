package com.thenoughtfox.orasulmeu.net.model.post

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostReactionsDto(

    @SerializedName("dislike")
    val dislike: kotlin.Int,

    @SerializedName("like")
    val like: kotlin.Int,

    @SerializedName("userReaction")
    val userReaction: UserReaction? = null

) : Parcelable {

    /**
     *
     *
     * Values: like,dislike
     */
    enum class UserReaction(val value: kotlin.String) {
        @SerializedName(value = "like")
        like("like"),
        @SerializedName(value = "dislike")
        dislike("dislike");
    }
}

