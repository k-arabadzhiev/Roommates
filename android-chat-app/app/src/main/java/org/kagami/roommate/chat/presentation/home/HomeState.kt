package org.kagami.roommate.chat.presentation.home

import org.kagami.roommate.chat.domain.model.user.RoommateProfile

data class HomeState(
    val isLoading: Boolean = false,
    val items: List<RoommateProfile> = emptyList(),
    val endReached: Boolean = false,
    val error: String? = null,
    val page: Int = 1,
    val index: Int = 0
)