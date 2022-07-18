package org.diploma.routes

import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.diploma.ChatServer
import org.diploma.data.chat.messages.ChatMessage
import org.diploma.data.matches.MatchesDataSource
import org.koin.ktor.ext.inject

fun Route.chatRoutes() {
    val db: MatchesDataSource by inject()
    val parser: Json by inject()
    val server: ChatServer by inject()

    webSocketChatRoute(server, db, parser)
}

fun Route.webSocketChatRoute(server: ChatServer, db: MatchesDataSource, parser: Json) {
    authenticate("auth-jwt") {
        webSocket("/messages/{match_id}/chat") {
            val matchId = call.parameters["match_id"] ?: run {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Match doesn't exist!"))
                return@webSocket
            }

            val match = db.findMatchById(matchId)
            if (match == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Match doesn't exist!"))
                return@webSocket
            }
            val sender = call.request.header("sender-id") ?: run {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Match doesn't exist!"))
                return@webSocket
            }
            val isParticipant = db.checkParticipant(matchId, sender)
            if (!isParticipant) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "You're not part of this thread!"))
                return@webSocket
            }

            try {
                server.joinRoom(matchId, sender, this)
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        val message = frame.readText()
                        val chatMessage: ChatMessage = parser.decodeFromString(message)

                        val receiver = match.participants.first { it != sender }
                        server.chatRooms[matchId]?.sendMessage(receiver, chatMessage)/*?.collect { status ->
//                            outgoing.send(Frame.Text(parser.encodeToString(status)))
                        }*/
                        val room = server.chatRooms[matchId]
                        room?.messages?.let {
                            db.insertMessages(matchId, it)
                            room.messages.clear()
                        }
                    }
                }
            } catch (e: Exception) {
                println(e.printStackTrace())
            } finally {
                // remove connection
                val room = server.chatRooms[matchId]
                if (room != null && room.participants.keys().toList().size == 1) {
                    db.insertMessages(matchId, room.messages)
                }
                room?.disconnect(sender)
                if (room?.participants?.isEmpty() == true) {
                    println("Removing empty room.")
                    server.removeEmptyRoom(matchId)
                }
                close(CloseReason(CloseReason.Codes.NORMAL, "Client left."))
            }
        }
    }
}