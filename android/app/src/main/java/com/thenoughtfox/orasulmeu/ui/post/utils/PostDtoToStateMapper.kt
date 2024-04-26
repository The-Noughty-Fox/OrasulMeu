package com.thenoughtfox.orasulmeu.ui.post.utils

import com.thenoughtfox.orasulmeu.ui.post.PostContract
import org.openapitools.client.models.PostDto

object PostDtoToStateMapper {
    fun PostDto.toState(): PostContract.State = PostContract.State(
        author = "${this.author.firstName} ${this.author.lastName}",
        title = this.title,
        textContent = this.content,
        media = this.media,
        reaction = PostContract.Reaction(
            selectedReaction = PostContract.Reactions.NOTHING,
            count = 0
        ), // todo get that value from backend
        address = "no data yet", // todo get from backend
        time = "no time yet" // todo get from backend
    )
}