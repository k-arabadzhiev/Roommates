package org.kagami.roommate.chat.presentation.chat

import org.kagami.roommate.chat.data.remote.ws.models.messages.ChatMessage

data class ChatState(
    val isLoading: Boolean = false,
    val messages: List<ChatMessage> = listOf(),
    val error: String? = null
)