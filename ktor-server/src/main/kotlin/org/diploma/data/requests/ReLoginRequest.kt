package org.diploma.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class ReLoginRequest(
    val id: String
)