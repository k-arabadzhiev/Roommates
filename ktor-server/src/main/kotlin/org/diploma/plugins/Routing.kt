package org.diploma.plugins

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.plugins.defaultheaders.*
import org.diploma.routes.*

fun Application.configureRouting() {
    install(DefaultHeaders)
    install(Routing) {
        authRoutes()
        roommateRoutes()
        matchRoutes()
        chatRoutes()
    }
}

