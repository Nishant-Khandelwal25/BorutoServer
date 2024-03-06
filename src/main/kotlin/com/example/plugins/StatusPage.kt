package com.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import javax.naming.AuthenticationException

fun Application.configureStatusPage() {
    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(message = "404: Page Not Found", status = status)
        }

        exception<AuthenticationException> { call, exception ->
            call.respond(message = "We caught an exception :${exception}", status = HttpStatusCode.Unauthorized)
        }
    }
}