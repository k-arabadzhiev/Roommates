package org.diploma.plugins

import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import org.bson.types.ObjectId
import org.diploma.data.user.UserDataSource
import org.diploma.data.user.UserSession
import org.diploma.security.token.TokenConfig
import org.koin.ktor.ext.inject
import java.time.Instant
import java.util.*

fun Application.configureSecurity() {

    val config: TokenConfig by inject()
    val db: UserDataSource by inject()

    install(Authentication) {
        jwt("auth-jwt") {
            realm = this@configureSecurity.environment.config.property("jwt.realm").getString()
            verifier(
                JWT
                    .require(Algorithm.HMAC256(config.secret))
                    .withAudience(config.audience)
                    .withIssuer(config.issuer)
                    .build()
            )
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asString()
                if (userId.isNotEmpty()) {
                    val expiresIn = credential.payload.getClaim("exp").asDate()
                    if (expiresIn.after(Date.from(Instant.now()))) {
                        val user = db.findUserById(ObjectId(userId))
                        if (user != null)
                            return@validate JWTPrincipal(credential.payload)
                    }
                }
                null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
        session<UserSession>("user_session") {
            validate { session ->
                val userId = db.findUserById(ObjectId(session.userId))
                if (userId != null) {
                    session
                } else {
                    null
                }
            }
            challenge {
                call.respondRedirect("/login")
            }
        }
    }

}
