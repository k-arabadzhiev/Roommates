package org.diploma.data.responses

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.diploma.data.chat.messages.ChatMessage
import org.litote.kmongo.Id

@Serializable
data class MatchResponse(
    @Contextual
    @SerialName("_id")
    val id: Id<String>,
    val participants: List<String>,
    val profile: RoommateProfile,
    val messages: List<ChatMessage>
)
