package org.diploma.plugins

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import org.diploma.data.user.UserSession
import java.io.File
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun Application.configureSessions() {

    install(Sessions) {
        val secret = this@configureSessions.environment.config.property("ktor.session_key").getString()
        val secretSignKey = hex(secret)
        cookie<UserSession>(
            "user_session",
            directorySessionStorage(File(".sessions"), cached = true)
        ) {
            cookie.maxAge = (30 * 6).toDuration(DurationUnit.DAYS)
            transform(SessionTransportTransformerMessageAuthentication(secretSignKey))
        }
    }
}
