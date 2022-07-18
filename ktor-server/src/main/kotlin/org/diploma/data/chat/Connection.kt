package org.diploma.data.chat

import io.ktor.websocket.*

data class ChatConnection(
    var socket: WebSocketSession
)
