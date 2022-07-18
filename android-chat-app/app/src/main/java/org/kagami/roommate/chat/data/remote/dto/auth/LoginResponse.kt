package org.kagami.roommate.chat.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String? = null,
    val message: String? = null
)
