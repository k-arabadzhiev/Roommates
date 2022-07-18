package org.kagami.roommate.chat.data.remote.dto.matches

import kotlinx.serialization.Serializable

@Serializable
data class MatchCheckResponse(
    val match: Boolean
)