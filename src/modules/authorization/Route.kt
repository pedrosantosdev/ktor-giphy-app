package io.pedro.santos.dev.modules.authorization

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.pedro.santos.dev.JsonResponse
import io.pedro.santos.dev.MissingParamsException
import io.pedro.santos.dev.modules.user.User

fun Route.authorization(){
    route("/oauth") {
        post("/auth") {
            val login = call.receive<PostLogin>()
            if (login.username.isNullOrEmpty() || login.password.isNullOrEmpty()) throw MissingParamsException()
            else call.respond(JsonResponse(HttpStatusCode.OK.value, AuthorizationService().authenticate(login)))
        }
        post("/register") {
            val login = call.receive<PostLogin>()
            if (login.username.isNullOrEmpty() || login.password.isNullOrEmpty()) throw MissingParamsException()
            else call.respond(JsonResponse(HttpStatusCode.OK.value, AuthorizationService().register(login)))
        }
        authenticate {
            get("/me") {
                val principal = call.authentication.principal<User>() ?: error("No principal decoded")
                call.respond(JsonResponse(HttpStatusCode.OK.value, mapOf("user" to principal)))
            }
        }
    }
}