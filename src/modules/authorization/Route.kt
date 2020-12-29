package io.pedro.santos.dev.modules.authorization

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.pedro.santos.dev.JsonResponse

fun Route.authorization(){
    route("/oauth") {
        post("/auth") {
            val postLogin = call.receive<PostLogin>()
            when (postLogin) {
                null -> call.respond(JsonResponse(400, mapOf("message" to "missing parameters"), "error"))
                else -> call.respond(JsonResponse(200, JwtConfig.accessToken(postLogin)))
            }
        }
        authenticate {
            get("/me") {
                val principal = call.principal<PostLogin>() ?: error("No principal decoded")
                call.respond(JsonResponse(200, mapOf("user" to principal)))
            }
        }
    }
}