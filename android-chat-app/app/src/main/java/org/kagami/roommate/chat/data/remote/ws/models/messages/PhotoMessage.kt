package org.kagami.roommate.chat.data.remote.ws.models.messages

import kotlinx.serialization.*

@Serializable
@SerialName("PhotoMessage")
data class PhotoMessage(
    @SerialName("_id")
    override val id: String,
    override val timestamp: Long,
    override val to: String,
    override val from: String,
    val url: String,
) : ChatMessage()
