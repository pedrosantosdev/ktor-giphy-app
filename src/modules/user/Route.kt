package io.pedro.santos.dev.modules.user

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.pedro.santos.dev.AuthorizationException
import io.pedro.santos.dev.JsonResponse
import io.pedro.santos.dev.MissingParamsException

fun Route.user() {
    authenticate {
        route("/users") {
            get {
                call.respond(JsonResponse(UserService().findAll()))
            }

            get ("/clear"){
                call.respond(JsonResponse(UserService().deleteDeactivated()))
            }

            post {
                val login = call.receive<Parameters>()
                if (login["username"].isNullOrEmpty() || login["password"].isNullOrEmpty()) throw MissingParamsException()
               call.respond(
                   JsonResponse(UserService().create(User( username = login["username"]!!, password = login["password"])))
               )
            }

            route("/{id}") {
                get {
                    val id = call.parameters["id"]?.toInt() ?: throw MissingParamsException()
                    call.respond(JsonResponse(UserService().findById(id)))
                }

                put {
                    val principal = call.authentication.principal<User>() ?: throw AuthorizationException()
                    val entity = call.receive<User>()
                    if (principal.username != entity.username) throw AuthorizationException()
                    call.respond(JsonResponse(UserService().update(entity)))
                }

                delete {
                    val id = call.parameters["id"]?.toInt() ?: throw MissingParamsException()
                    call.respond(JsonResponse(UserService().deleteById(id)))
                }
            }
        }
    }
}