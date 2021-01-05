package io.pedro.santos.dev.modules.user

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.pedro.santos.dev.JsonResponse
import io.pedro.santos.dev.MissingParamsException

fun Route.user() {
    authenticate {
        route("/users") {
            get {
                call.respond(JsonResponse(HttpStatusCode.OK.value, UserService().findAll()))
            }

            post {
                val login = call.receive<User>()
                if (login.username == "" || login.password == "") throw MissingParamsException()
                else call.respond(JsonResponse(HttpStatusCode.OK.value, UserService().create(login)))
            }

            route("/{id}") {
                get {
                    val id = call.parameters["id"]?.toInt() ?: 0
                    call.respond(JsonResponse(HttpStatusCode.OK.value, UserService().findById(id)))
                }

                put {
                    val id = call.parameters["id"]?.toInt() ?: 0
                    val entity = call.receive<User>()
                    call.respond(JsonResponse(HttpStatusCode.OK.value, UserService().update(entity)))
                }

                delete {
                    val id = call.parameters["id"]?.toInt() ?: 0
                    call.respond(JsonResponse(HttpStatusCode.OK.value, UserService().deleteById(id)))
                }
            }
        }
    }
}