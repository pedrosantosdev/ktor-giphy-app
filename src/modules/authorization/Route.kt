package io.pedro.santos.dev.modules.authorization

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.pedro.santos.dev.JsonResponse

fun Route.authorization(){
    route("/oauth") {
        post("/auth") {
            val login = call.receive<PostLogin>()
            if (login.username == null || login.password == null) {
                call.respond(JsonResponse(HttpStatusCode.BadRequest.value, mapOf("message" to "missing parameters"), "error"))
            } else {
                val token = AuthorizationService().authenticate(login)
                if (token != null) {
                    call.respond(JsonResponse(HttpStatusCode.OK.value, token))
                } else {
                    call.respond(
                        JsonResponse(
                            HttpStatusCode.Unauthorized.value,
                            mapOf("message" to "unauthorized access"),
                            "error"
                        )
                    )
                }
            }
        }
        authenticate {
            get("/me") {
                val principal = call.principal<PostLogin>() ?: error("No principal decoded")
                call.respond(JsonResponse(HttpStatusCode.OK.value, mapOf("user" to principal)))
            }
        }
    }
}