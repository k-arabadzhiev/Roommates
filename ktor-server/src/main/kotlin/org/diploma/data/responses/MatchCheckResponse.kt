package org.diploma.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class MatchCheckResponse(
    val match: Boolean
)