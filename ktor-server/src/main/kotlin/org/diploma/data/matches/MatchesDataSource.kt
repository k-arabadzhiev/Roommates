package org.diploma.data.matches

import org.diploma.data.chat.messages.ChatMessage

interface MatchesDataSource {

    suspend fun insertMatch(match: Match): String
    suspend fun insertMessages(matchId: String, message: List<ChatMessage>)
    suspend fun getMatchMessages(id: String, count: Int): List<ChatMessage>
    suspend fun findMatchById(chatRoomId: String): Match?
    suspend fun checkParticipant(matchId: String, participantId: String): Boolean
}