package org.kagami.roommate.chat.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class NewUserRequest(
    val username: String,
    val password: String,
    val name: String,
    val age: Int,
    val gender: Int
)
