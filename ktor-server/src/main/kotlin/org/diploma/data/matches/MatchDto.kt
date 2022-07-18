package org.diploma.data.matches

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.diploma.data.chat.messages.ChatMessage
import org.diploma.data.responses.RoommateProfile

@Serializable
data class MatchDto(
    val id: String,
    val participant: RoommateProfile,
    @Contextual
    val messages: ChatMessage
)
