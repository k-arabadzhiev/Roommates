package org.diploma.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.bson.types.ObjectId
import org.diploma.data.chat.messages.TextMessage
import org.diploma.data.matches.Match
import org.diploma.data.matches.MatchDto
import org.diploma.data.matches.MatchesDataSource
import org.diploma.data.user.UserDataSource
import org.diploma.data.user.UserSession
import org.koin.ktor.ext.inject
import org.litote.kmongo.newId
import org.litote.kmongo.toId
import java.time.Instant

fun Route.matchRoutes() {
    val db: UserDataSource by inject()
    val matchDb: MatchesDataSource by inject()

    getMatchMessages(matchDb)
    like(db, matchDb)
    pass(db)
    getMatches(db, matchDb)
}

fun Route.getMatchMessages(db: MatchesDataSource) {
    authenticate("auth-jwt") {
        get("/matches/{match_id}/messages") {
            val count = call.request.queryParameters["count"]
            val id = call.parameters["match_id"]

            if (count == null || id == null) {
                call.respond(HttpStatusCode.Conflict, "Missing or invalid parameter!")
                return@get
            }
            val session = call.sessions.get<UserSession>()
            if (session == null) {
                call.respondRedirect("/login")
            } else {
                if (id.contains(session.userId)) {
                    val messages = db.getMatchMessages(id, count.toInt())
                    call.respond(HttpStatusCode.OK, messages)
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "You're not allowed to see this conversation!")
                }
            }
        }
    }
}

fun Route.like(db: UserDataSource, matchDb: MatchesDataSource) {
    authenticate("auth-jwt") {
        get("like/{id}") {
            val roommateId = call.parameters["id"]

            val session = call.sessions.get<UserSession>()
            if (session == null) {
                call.respond(HttpStatusCode.Unauthorized, "No session!")
                return@get
            }
            val userId = session.userId

            if (roommateId == null) {
                call.respond(HttpStatusCode.Conflict, "Missing or invalid parameter!")
                return@get
            }
            val matchResult = db.isMatch(userId, roommateId)

            if (matchResult.match) {
                db.addToLikes(userId, roommateId)
                val match = Match(
                    id = "$roommateId$userId".toId(),
                    participants = listOf(roommateId, userId),
                    messages = listOf()
                )
                val matchId = matchDb.insertMatch(match)
                if (matchId.isNotEmpty()) {
                    db.updateMatches(userId, roommateId, match)
                    call.respond(HttpStatusCode.OK, matchResult)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, matchResult)
                }
            } else {
                val result = db.addToLikes(userId, roommateId)
                if (result) {
                    call.respond(HttpStatusCode.OK, matchResult)
                } else {
                    call.respond(HttpStatusCode.NotModified, matchResult)
                }
            }
        }
    }
}

fun Route.pass(db: UserDataSource) {
    authenticate("auth-jwt") {
        get("pass/{id}") {
            val roommateId = call.parameters["id"]
            if (roommateId == null) {
                call.respond(HttpStatusCode.Conflict, "Missing or invalid parameter!")
                return@get
            }
            val session = call.sessions.get<UserSession>()
            if (session == null) {
                call.respond(HttpStatusCode.Unauthorized, "No session!")
                return@get
            }
            val userId = session.userId
            db.addToPasses(userId, roommateId)
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getMatches(db: UserDataSource, matchDb: MatchesDataSource) {
    authenticate("auth-jwt") {
        get("matches") {
            val session = call.sessions.get<UserSession>()
            if (session == null) {
                call.respond(HttpStatusCode.Unauthorized, "No session")
            }
            val user = db.findUserById(ObjectId(session?.userId))
            if (user == null) {
                call.respond(HttpStatusCode.NotFound, "No user")
            }
            val matches = user?.matches?.map { match ->
                val participantId = match.participants.find {
                    it != user.id.toHexString()
                }
                val participant = db.findProfileById(ObjectId(participantId!!))
                val messages = matchDb.getMatchMessages(match.id.toString(), 1).firstOrNull()
                MatchDto(
                    id = match.id.toString(),
                    participant = participant!!,
                    messages = messages ?: TextMessage(
                        id = newId(),
                        timestamp = Instant.now().toEpochMilli(),
                        to = user.id.toHexString(),
                        from = participantId,
                        content = "Start a conversation"
                    )
                )
            }
            if (matches != null) {
                call.respond(HttpStatusCode.OK, matches)
            } else {
                call.respond(HttpStatusCode.OK, listOf<MatchDto>())
            }
        }
    }
}