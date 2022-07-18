package org.kagami.roommate.chat.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class ReLoginRequest(
    val id: String
)
