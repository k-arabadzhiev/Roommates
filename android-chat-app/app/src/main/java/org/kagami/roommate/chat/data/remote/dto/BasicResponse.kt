package org.kagami.roommate.chat.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class BasicResponse(
    val status: String? = null,
    val message: String
)