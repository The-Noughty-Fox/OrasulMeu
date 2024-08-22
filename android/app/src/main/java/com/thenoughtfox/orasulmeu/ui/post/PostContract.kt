package com.thenoughtfox.orasulmeu.ui.post

import org.openapitools.client.models.MediaSupabaseDto

object PostContract {

    data class State(
        val id: Int = 0,
        val author: String = "",
        val title: String = "",
        val textContent: String = "",
        val media: List<MediaSupabaseDto> = emptyList(),
        val reaction: Reaction = Reaction(Reactions.NOTHING),
        val address: String = "",
        val time: String = ""
    )

    sealed interface Event {
        data object Like : Event
        data object Dislike : Event
        data object RevokeReaction : Event
        data object ConfirmReport : Event
        data object Delete : Event
        data object Edit : Event
    }

    enum class Reactions {
        LIKE, DISLIKE, NOTHING
    }

    data class Reaction(
        val selectedReaction: Reactions,
        val likes: Int = 0,
        val dislikes: Int = 0
    )
}