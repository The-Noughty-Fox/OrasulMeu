package com.thenoughtfox.orasulmeu.ui.post

import org.openapitools.client.models.Media

interface PostContract {

    data class State(
        val author: String = "",
        val title: String = "",
        val textContent: String = "",
        val media: List<Media> = emptyList(),
        val reaction: Reaction = Reaction(Reactions.NOTHING),
        val isReactionLoading: Boolean = false,
        val address: String = "",
        val time: String = ""
    )

    sealed interface Event {
        data object Like : Event
        data object Dislike : Event
        data object RevokeReaction : Event
        data object Report : Event
        data object ConfirmReport : Event
    }

    sealed interface Action {
        data object RequestReportConfirmation : Action
        data class UpdateReaction(val newValue: Reaction) : Action
    }

    enum class Reactions {
        LIKE, DISLIKE, NOTHING
    }

    data class Reaction(val selectedReaction: Reactions, val count: Int = 0)
}