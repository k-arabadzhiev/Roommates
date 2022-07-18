package org.diploma.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class BasicResponse(
    val status: String? = null,
    val message: String,
)
