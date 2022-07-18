package org.diploma.data.chat.messages

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id

@Serializable
@SerialName("ChatMessage")
sealed class ChatMessage {
    @Contextual
    @SerialName("_id")
    abstract val id: Id<ChatMessage>
    abstract val timestamp: Long
    abstract val to: String
    abstract val from: String
}
