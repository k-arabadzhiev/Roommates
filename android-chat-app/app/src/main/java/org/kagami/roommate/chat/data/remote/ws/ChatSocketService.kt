package org.kagami.roommate.chat.data.remote.ws

import kotlinx.coroutines.flow.Flow
import org.kagami.roommate.chat.data.remote.ws.models.messages.ChatMessage
import org.kagami.roommate.chat.util.ApiResult

interface ChatSocketService {

    suspend fun initSession(
        token: String, matchId: String, senderId: String
    ): ApiResult<Unit>

    suspend fun sendMessage(message: ChatMessage)
    suspend fun observeMessages(): Flow<ChatMessage>
    suspend fun disconnect()
    suspend fun checkForImage(url: String): Boolean
}