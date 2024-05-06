package com.thenoughtfox.orasulmeu.ui.post.utils

import com.thenoughtfox.orasulmeu.ui.post.PostContract
import org.openapitools.client.models.PostDto
import org.openapitools.client.models.PostReactionsDto

object PostDtoToStateMapper {
    fun PostDto.toState(): PostContract.State = PostContract.State(
        author = "${this.author.firstName} ${this.author.lastName}",
        title = this.title,
        textContent = this.content,
        media = this.media,
        reaction = this.reactions.mapReaction(),
        address = this.locationAddress,
        time = "no time yet" // todo get from backend
    )

    private fun PostReactionsDto.mapReaction(): PostContract.Reaction {
        return when (this.userReaction) {
            PostReactionsDto.UserReaction.like -> PostContract.Reaction(
                selectedReaction = PostContract.Reactions.LIKE,
                count = this.like
            )

            PostReactionsDto.UserReaction.dislike -> PostContract.Reaction(
                selectedReaction = PostContract.Reactions.DISLIKE,
                count = this.dislike
            )

            null -> PostContract.Reaction(PostContract.Reactions.NOTHING, 0)
        }
    }
}