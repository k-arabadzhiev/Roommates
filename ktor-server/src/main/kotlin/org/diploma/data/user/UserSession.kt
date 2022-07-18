package org.diploma.data.user

import io.ktor.server.auth.*

data class UserSession(
    val clientId: String,
    val userId: String
) : Principal
