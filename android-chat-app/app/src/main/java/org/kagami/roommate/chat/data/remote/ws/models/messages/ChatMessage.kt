package org.kagami.roommate.chat.data.remote.ws.models.messages

import kotlinx.serialization.*

@Serializable
@SerialName("ChatMessage")
sealed class ChatMessage {
    @SerialName("_id")
    abstract val id: String
    abstract val timestamp: Long
    abstract val to: String
    abstract val from: String
}
