package org.kagami.roommate.chat.data.remote.ws.models.messages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("TextMessage")
data class TextMessage(
    @SerialName("_id")
    override val id: String,
    override val timestamp: Long,
    override val to: String,
    override val from: String,
    val content: String,
) : ChatMessage()
