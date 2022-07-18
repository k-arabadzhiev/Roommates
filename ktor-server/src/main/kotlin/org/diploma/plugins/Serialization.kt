package org.diploma.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.application.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject

fun Application.configureSerialization() {
    val jsonConfig: Json by inject()
    install(ContentNegotiation) {
        json(json = jsonConfig)
    }
}
