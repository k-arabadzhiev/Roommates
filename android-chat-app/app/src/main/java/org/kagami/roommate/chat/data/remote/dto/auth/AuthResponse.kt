package org.kagami.roommate.chat.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val status: String,
    val token: String
)
