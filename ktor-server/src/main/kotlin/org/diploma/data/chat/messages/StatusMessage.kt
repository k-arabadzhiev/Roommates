package org.diploma.data.chat.messages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("StatusMessage")
sealed class StatusMessage {
    abstract val messageId: String
}

@Serializable
@SerialName("Sent")
data class Sent(
    override val messageId: String
) : StatusMessage()

@Serializable
@SerialName("Delivered")
data class Delivered(
    override val messageId: String
) : StatusMessage()

@Serializable
@SerialName("Seen")
data class Seen(
    override val messageId: String
) : StatusMessage()