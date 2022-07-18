package org.diploma

import io.ktor.server.websocket.*
import org.diploma.data.chat.ChatRoom
import java.util.concurrent.ConcurrentHashMap

class ChatServer {

    val chatRooms = ConcurrentHashMap<String, ChatRoom>()

    private fun createRoom(matchId: String) {
        if (!chatRooms.containsKey(matchId)) {
            chatRooms[matchId] = ChatRoom()
            println("Room for $matchId created.")
        }
    }

    fun joinRoom(matchId: String, participant: String, socket: DefaultWebSocketServerSession) {
        createRoom(matchId)
        if (chatRooms.containsKey(matchId)) {
            chatRooms[matchId]!!.join(participant, socket)
            println("room:$matchId\nparticipant $participant joined. ")
        }
    }

    fun removeEmptyRoom(matchId: String) {
        chatRooms.remove(matchId)
    }

}