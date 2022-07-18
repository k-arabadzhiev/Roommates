package org.kagami.roommate.chat.domain.model.user

import androidx.room.PrimaryKey
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.kagami.roommate.chat.data.remote.ws.models.messages.ChatMessage

@Serializable
data class Match(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val participant: RoommateProfile,
    @Contextual
    val messages: ChatMessage
)
