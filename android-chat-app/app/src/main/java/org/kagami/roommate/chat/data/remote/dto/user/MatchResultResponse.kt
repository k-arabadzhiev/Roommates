package org.kagami.roommate.chat.data.remote.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class MatchResultResponse(
    val match: Boolean
)
