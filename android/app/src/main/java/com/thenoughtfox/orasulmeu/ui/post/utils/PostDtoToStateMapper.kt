package com.thenoughtfox.orasulmeu.ui.post.utils

import com.thenoughtfox.orasulmeu.ui.post.PostContract
import org.openapitools.client.models.PostDto
import org.openapitools.client.models.PostReactionsDto

object PostDtoToStateMapper {
    fun PostDto.toState(): PostContract.State = PostContract.State(
        id = this.id,
        author = "${this.author.firstName} ${this.author.lastName}",
        title = this.title,
        textContent = this.content,
        media = this.media,
        reaction = this.reactions.mapReaction(),
        address = this.locationAddress,
        time = "no time yet" // todo get from backend
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
}