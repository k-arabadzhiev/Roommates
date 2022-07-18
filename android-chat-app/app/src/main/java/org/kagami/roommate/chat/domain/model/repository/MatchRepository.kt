package org.kagami.roommate.chat.domain.model.repository

import org.kagami.roommate.chat.data.remote.ws.models.messages.ChatMessage
import org.kagami.roommate.chat.domain.model.user.Match

interface MatchRepository {

    suspend fun fetchMatches(token: String): List<Match>
    suspend fun getMatchMessages(token: String, matchId: String): List<ChatMessage>
}