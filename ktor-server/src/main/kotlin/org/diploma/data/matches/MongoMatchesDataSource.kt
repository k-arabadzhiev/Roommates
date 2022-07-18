package org.diploma.data.matches

import com.mongodb.client.model.PushOptions
import org.diploma.data.chat.messages.ChatMessage
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase

class MongoMatchesDataSource(
    db: CoroutineDatabase
) : MatchesDataSource {

    private val matches = db.getCollection<Match>()

    override suspend fun insertMatch(match: Match): String {
        return matches.insertOne(match).insertedId?.toString() ?: ""
    }

    override suspend fun insertMessages(matchId: String, message: List<ChatMessage>) {
        matches.updateOneById(
            id = matchId,
            update = pushEach(Match::messages, message, PushOptions().position(0))
        )
    }

    override suspend fun getMatchMessages(id: String, count: Int): List<ChatMessage> {
        val query = matches.find(
            Match::id eq id.toId()
        ).projection(
            Match::messages.slice(count)
        ).first()
        return query?.messages ?: listOf()
    }

    override suspend fun findMatchById(chatRoomId: String): Match? {
        return matches.find(
            Match::id eq chatRoomId.toId()
        ).first()
    }

    override suspend fun checkParticipant(matchId: String, participantId: String): Boolean {
        val query = matches.find(
            Match::id eq matchId.toId()
        ).first()
        return query?.participants?.contains(participantId) == true
    }
}