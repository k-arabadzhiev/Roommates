package org.diploma.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class NewUserRequest(
    val username: String,
    val password: String,
    val name: String,
    val age: Int,
    val gender: Int
)
