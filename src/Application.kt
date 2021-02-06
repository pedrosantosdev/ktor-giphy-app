package io.pedro.santos.dev

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.pedro.santos.dev.modules.authorization.JwtConfig
import io.pedro.santos.dev.modules.authorization.authorization
import io.pedro.santos.dev.modules.common.DatabaseFactory
import io.pedro.santos.dev.modules.user.UserRepository
import io.pedro.santos.dev.modules.user.user
import java.lang.reflect.Modifier
import java.text.DateFormat

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        host("localhost:8080", schemes = listOf("http", "https"))
        host("pedrosantosdev.github.io/giphy-app/", schemes = listOf("https"))
        allowCredentials = true
        allowNonSimpleContentTypes = true
    }

    install(DefaultHeaders)
    install(CallLogging)

    install(StatusPages) {
        status(HttpStatusCode.NotFound) {
            call.respond(HttpStatusCode.NotFound, JsonResponse( mapOf("message" to "not found"), "error"))
        }
        exception<AuthenticationException> { cause ->
            call.respond(HttpStatusCode.Unauthorized, JsonResponse(mapOf("message" to cause.message), "error"))
        }
        exception<AuthorizationException> { cause ->
            call.respond(HttpStatusCode.Forbidden, JsonResponse(mapOf("message" to cause.message), "error"))
        }
        exception<MissingParamsException> { cause ->
            call.respond(HttpStatusCode.UnprocessableEntity, JsonResponse(mapOf("message" to cause.message), "error"))
        }
        exception<BadRequestException> { cause ->
            call.respond(HttpStatusCode.BadRequest, JsonResponse(mapOf("message" to cause.message), "error"))
        }
        exception<Throwable> { cause -> call.respond(HttpStatusCode.InternalServerError, JsonResponse(mapOf("message" to cause.message), "error")) }
    }

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            disableHtmlEscaping()
            excludeFieldsWithModifiers(Modifier.TRANSIENT)
            setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        }
    }

    install(Authentication) {
        jwt {
            this.realm = JwtConfig.realm
            verifier(JwtConfig.verifier)
            validate {
                it.payload.getClaim("username").asString()?.let { username ->
                    UserRepository().findByUsername(username)
                }
            }
        }
    }

    DatabaseFactory.init()

    routing {
        get("/") {
            call.respond(HttpStatusCode.OK, JsonResponse(mapOf("message" to "API ACTIVE!")))
        }
        authorization()
        user()
    }
}

data class JsonResponse<T>(val data: T, val message: String = "Success")
data class AuthenticationException(override val message: String = "Authentication failed") : Exception()
data class AuthorizationException(override val message: String = "You are not authorised to use this service") : Exception()
data class MissingParamsException(override val message: String = "Missing Params") : Exception()
data class BadRequestException(override val message: String = "Bad Request") : Exception()
