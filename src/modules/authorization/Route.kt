package io.pedro.santos.dev.modules.authorization

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.pedro.santos.dev.AuthorizationException
import io.pedro.santos.dev.JsonResponse
import io.pedro.santos.dev.MissingParamsException
import io.pedro.santos.dev.modules.user.User

fun Route.authorization(){
    route("/oauth") {
        post("/auth") {
            val login = call.receive<Parameters>()
            if (login["username"].isNullOrEmpty() || login["password"].isNullOrEmpty()) throw MissingParamsException()
            else call.respond(JsonResponse(AuthorizationService().authenticate(PostLogin(username = login["username"]!!, password = login["password"]!!))))
        }
        post("/register") {
            val login = call.receive<Parameters>()
            if (login["username"].isNullOrEmpty() || login["password"].isNullOrEmpty()) throw MissingParamsException()
            else call.respond(JsonResponse(AuthorizationService().register(PostLogin(username = login["username"]!!, password = login["password"]!!))))
        }
        post("/refresh-token") {
            val token = call.receive<Parameters>()["token"]
            if (token.isNullOrEmpty()) throw AuthorizationException()
            else call.respond(JsonResponse(AuthorizationService().refreshToken(token)))
        }
        authenticate {
            get("/me") {
                val principal = call.authentication.principal<User>() ?: error("No principal decoded")
                call.respond(JsonResponse(mapOf("user" to principal)))
            }
        }
    }
}