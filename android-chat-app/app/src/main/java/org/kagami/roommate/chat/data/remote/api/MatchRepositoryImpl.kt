package org.kagami.roommate.chat.data.remote.api

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.network.sockets.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.kagami.roommate.chat.data.local.UserDatabase
import org.kagami.roommate.chat.data.remote.ws.models.messages.ChatMessage
import org.kagami.roommate.chat.domain.model.repository.MatchRepository
import org.kagami.roommate.chat.domain.model.user.Match
import org.kagami.roommate.chat.util.Constants
import org.kagami.roommate.chat.util.Constants.MATCHES_ENDPOINT
import org.kagami.roommate.chat.util.Constants.MESSAGES_COUNT
import javax.inject.Inject


class MatchRepositoryImpl @Inject constructor(
    private val client: HttpClient
) : MatchRepository {

    override suspend fun fetchMatches(token: String): List<Match> {
        return try {
            val response = client.get(MATCHES_ENDPOINT) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            if (response.status.isSuccess()) {
                val rawMatches = response.body<List<Match>>()
                val matches = mutableListOf<Match>()
                rawMatches.toMutableList().forEach { match ->
                    val photos = match.participant.photos.map { url ->
                        if (url.startsWith("http")) {
                            url
                        } else {
                            "${Constants.BASE_URL}/$url"
                        }
                    }
                    val participant = match.participant.copy(photos = photos)
                    matches.add(match.copy(participant = participant))
                }
                matches.toList()
            } else
                listOf()
        } catch (e: ConnectTimeoutException) {
            Log.d("fetchMatches", "${e.message}")
            listOf()
        }
    }

    override suspend fun getMatchMessages(token: String, matchId: String): List<ChatMessage> {
        return try {
            val response = client.get("$MATCHES_ENDPOINT/$matchId/messages") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                url {
                    parameters.append("count", MESSAGES_COUNT.toString())
                }
            }
            if (response.status.isSuccess()) {
                response.body()
            } else {
                listOf()
            }
        } catch (e: ConnectTimeoutException) {
            Log.d("getMatchMessages", "${e.message}")
            listOf()
        }
    }
}