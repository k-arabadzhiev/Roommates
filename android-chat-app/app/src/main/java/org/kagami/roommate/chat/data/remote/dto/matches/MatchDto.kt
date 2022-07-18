package org.kagami.roommate.chat.data.remote.dto.matches

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import org.kagami.roommate.chat.data.remote.ws.models.messages.ChatMessage

@Serializable
data class MatchDto(
    @Contextual
    @SerialName("_id")
    val id: ObjectId,
    val participants: List<String>,
    @Contextual
    val messages: List<ChatMessage>
)
