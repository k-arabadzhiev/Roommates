package org.diploma

import io.ktor.server.application.*
import org.diploma.plugins.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

// application.conf references the main function.
// This annotation prevents the IDE from marking it as unused.
@Suppress("unused")
fun Application.module() {
    configureDI()
    configureSessions()
    configureSerialization()
    configureSockets()
    configureRouting()
    configureSecurity()
    configureMonitoring()
}

