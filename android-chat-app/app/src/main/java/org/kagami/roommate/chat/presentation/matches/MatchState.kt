package org.kagami.roommate.chat.presentation.matches

import org.kagami.roommate.chat.domain.model.user.Match

data class MatchState(
    val isLoading: Boolean = false,
    val matches: List<Match> = emptyList()
)