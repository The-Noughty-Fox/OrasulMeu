package com.thenoughtfox.orasulmeu.ui.post.utils

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.thenoughtfox.orasulmeu.ui.post.PostContract
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.openapitools.client.models.PostDto
import org.openapitools.client.models.PostReactionsDto

object PostDtoToStateMapper {
    fun PostDto.toState(): PostContract.State = PostContract.State(
        id = this.id,
        author = this.author.username,
        title = this.title,
        textContent = this.content,
        media = this.media,
        reaction = this.reactions.mapReaction(),
        address = this.locationAddress,
        time = DateTime.parse(this.createdAt).withZone(DateTimeZone.getDefault())
            .toString("HH:mm - dd.MM.yyyy")
    )

    private fun PostReactionsDto.mapReaction(): PostContract.Reaction {
        val selectedReaction = when (this.userReaction) {
            PostReactionsDto.UserReaction.like -> PostContract.Reactions.LIKE
            PostReactionsDto.UserReaction.dislike -> PostContract.Reactions.DISLIKE
            null -> PostContract.Reactions.NOTHING
        }

        return PostContract.Reaction(
            selectedReaction = selectedReaction,
            likes = this.like,
            dislikes = this.dislike
        )
    }


    fun PostDto.toJsonElement(): JsonElement {
        val gson = Gson()
        val jsonString = gson.toJson(this)
        return gson.fromJson(jsonString, JsonElement::class.java)
    }

    fun JsonElement.toPostDto(): PostDto? {
        return try {
            val gson = Gson()
            gson.fromJson(this, PostDto::class.java)
        } catch (e: Exception) {
            null
        }
    }
}