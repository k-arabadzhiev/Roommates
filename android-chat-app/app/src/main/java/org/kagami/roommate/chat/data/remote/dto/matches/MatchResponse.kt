package org.kagami.roommate.chat.data.remote.dto.matches

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import org.kagami.roommate.chat.data.remote.ws.models.messages.ChatMessage
import org.kagami.roommate.chat.domain.model.user.RoommateProfile

@Serializable
data class MatchResponse(
    @Contextual
    @SerialName("_id")
    val id: ObjectId,
    val participants: List<String>,
    val profile: RoommateProfile,
    val messages: List<ChatMessage>
)
