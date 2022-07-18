package org.diploma.plugins

import io.ktor.server.application.*
import org.diploma.di.mainModule
import org.koin.fileProperties
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureDI()  {

    install(Koin) {
        slf4jLogger()
        modules(mainModule).fileProperties()
    }
}