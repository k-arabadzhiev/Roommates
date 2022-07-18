package org.diploma.data.chat.messages

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id

@Serializable
@SerialName("TextMessage")
data class TextMessage(
    @Contextual
    @SerialName("_id")
    override val id: Id<ChatMessage>,
    override val timestamp: Long,
    override val to: String,
    override val from: String,
    val content: String,
) : ChatMessage()
