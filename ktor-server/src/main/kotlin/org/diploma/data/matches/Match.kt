package org.diploma.data.matches

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.diploma.data.chat.messages.ChatMessage
import org.litote.kmongo.Id

@Serializable
data class Match(
    @Contextual
    @SerialName("_id")
    val id: Id<Match>,
    val participants: List<String>,
    @Contextual
    val messages: List<ChatMessage>
)
