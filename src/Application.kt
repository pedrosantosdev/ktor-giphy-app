package io.pedro.santos.dev

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.gson.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.pedro.santos.dev.modules.authorization.JwtConfig
import io.pedro.santos.dev.modules.authorization.PostLogin
import io.pedro.santos.dev.modules.authorization.authorization
import java.lang.RuntimeException

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(DefaultHeaders)
    install(CallLogging)

    install(StatusPages) {
        status(HttpStatusCode.NotFound) {
            call.respond(HttpStatusCode.NotFound, JsonResponse(404, mapOf("message" to "not found"), "error"))
        }

        exception<Throwable> { cause -> call.respond(HttpStatusCode.NotFound, mapOf("message" to cause.message, "code" to HttpStatusCode.NotFound)) }
    }

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            disableHtmlEscaping()
            setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        }
    }

    install(Authentication) {
        jwt {
            this.realm = JwtConfig.realm
            verifier(JwtConfig.verifier)
            validate {
                if(it.payload.getClaim("username").asString().isNullOrEmpty()){
                    null
                } else {
                   JWTPrincipal(it.payload)
                }
            }
        }
    }

    /* val client = HttpClient() {
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
    } */
    // runBlocking {
        // Sample for making a HTTP Client request
        /*
        val message = client.post<JsonSampleClass> {
            url("http://127.0.0.1:8080/path/to/endpoint")
            contentType(ContentType.Application.Json)
            body = JsonSampleClass(hello = "world")
        }
        */
    // }

    routing {
        get("/") {
            call.respond(HttpStatusCode.BadRequest)
        }
        authorization()
    }
}

data class JsonResponse<T>(val status: Short = 200, val data: T, val message: String = "Success")
