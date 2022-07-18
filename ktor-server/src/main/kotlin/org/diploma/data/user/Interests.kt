package org.diploma.data.user

import kotlinx.serialization.Serializable

@Serializable
data class Interests(
    val id: Int,
    val name: String
)