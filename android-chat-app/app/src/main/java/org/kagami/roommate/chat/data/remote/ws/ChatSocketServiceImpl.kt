package org.kagami.roommate.chat.data.remote.ws

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.kagami.roommate.chat.data.remote.ws.models.messages.ChatMessage
import org.kagami.roommate.chat.util.ApiResult
import org.kagami.roommate.chat.util.Constants.BASE_URL_WS
import org.kagami.roommate.chat.util.Constants.CHAT_ENDPOINT_PATH
import org.kagami.roommate.chat.util.Constants.MESSAGE_ENDPOINT_PATH
import org.kagami.roommate.chat.util.Constants.SENDER_ID
import javax.inject.Inject

class ChatSocketServiceImpl @Inject constructor(
    private val client: HttpClient,
    private val parser: Json
) : ChatSocketService {

    private var socket: WebSocketSession? = null

    override suspend fun initSession(
        token: String,
        matchId: String,
        senderId: String
    ): ApiResult<Unit> {
        return try {
            socket = client.webSocketSession {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                    append(SENDER_ID, senderId)
                }
                url(BASE_URL_WS + MESSAGE_ENDPOINT_PATH + matchId + CHAT_ENDPOINT_PATH)
            }
            if (socket?.isActive == true) {
                ApiResult.Success(Unit)
            } else ApiResult.Error(message = "Couldn't start socket session!")
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error("Couldn't start socket session!")
        }
    }

    override suspend fun sendMessage(message: ChatMessage) {
        try {
            val parsedMessage = parser.encodeToString(message)
            socket?.send(Frame.Text(parsedMessage))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun observeMessages(): Flow<ChatMessage> {
        return try {
            socket?.incoming?.receiveAsFlow()?.filter {
                it is Frame.Text
            }?.map {
                val text = (it as Frame.Text).readText()
                val parsedMessage = parser.decodeFromString<ChatMessage>(text)
                parsedMessage
            } ?: flow {}
        } catch (e: Exception) {
            e.printStackTrace()
            flow {}
        }
    }

    override suspend fun checkForImage(url: String): Boolean {
        return try {
            val contentType = client.get(url).contentType()
            contentType != null && (contentType == ContentType.Image.JPEG || contentType == ContentType.Image.PNG)
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun disconnect() {
        socket?.close()
    }
}