package com.thenoughtfox.orasulmeu.ui.post

import org.openapitools.client.models.Media

interface PostContract {

    data class State(
        val id: Int = 0,
        val author: String = "",
        val title: String = "",
        val textContent: String = "",
        val media: List<Media> = emptyList(),
        val reaction: Reaction = Reaction(Reactions.NOTHING),
        val address: String = "",
        val time: String = ""
    )

    sealed interface Action {
        data object Like : Action
        data object Dislike : Action
        data object RevokeReaction : Action
        data object ConfirmReport : Action
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