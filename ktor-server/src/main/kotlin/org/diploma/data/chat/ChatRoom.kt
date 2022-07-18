package org.diploma.data.chat

import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.diploma.data.chat.messages.ChatMessage
import org.diploma.data.chat.messages.Delivered
import org.diploma.data.chat.messages.Sent
import org.diploma.data.chat.messages.StatusMessage
import org.koin.java.KoinJavaComponent.inject
import java.util.concurrent.ConcurrentHashMap

class ChatRoom {

    val messages: MutableList<ChatMessage> = mutableListOf()
    val participants = ConcurrentHashMap<String, ChatConnection>()
    private val parser: Json by inject(Json::class.java)

/*    suspend fun sendMessage(receiver: String, message: ChatMessage): Flow<StatusMessage> = flow {
        if (receiver.isNotEmpty()) {
            val frame = Frame.Text(parser.encodeToString(message))
            val receiverSocket = participants[receiver]?.socket
            if (receiverSocket != null) {
                participants[receiver]?.socket?.send(frame)
                messages.add(0, message)
                emit(Delivered(message.id.toString()))
            } else {
                messages.add(0, message)
                emit(Sent(message.id.toString()))
            }
        } else {
            emit(Sent(message.id.toString()))
        }
    }*/

    suspend fun sendMessage(receiver: String, message: ChatMessage) {
        if (receiver.isNotEmpty()) {
            val frame = Frame.Text(parser.encodeToString(message))
            val receiverSocket = participants[receiver]?.socket
            if (receiverSocket != null) {
                participants[receiver]?.socket?.send(frame)
                messages.add(0, message)
            } else {
                messages.add(0, message)
            }
        }
    }

    fun join(participant: String, socket: DefaultWebSocketServerSession) {
        participants[participant] = ChatConnection(socket)
    }

    fun disconnect(participant: String) {
        println("Disconnection participant: $participant\n")
        participants.remove(participant)
    }
}
