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
        exception<Throwable> { e ->
            call.respondText(e.localizedMessage,ContentType.Text.Plain, HttpStatusCode.InternalServerError)
        }
    }

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            disableHtmlEscaping()
        }
    }

    val realm = environment.config.property("jwt.realm").getString()
    install(Authentication) {
        jwt {
            this.realm = realm
            verifier(JwtConfig.verifier)
            validate {
                val username = it.payload.getClaim("username").asString()
                val password = it.payload.getClaim("password").asString()
                if (username != null && password != null) {
                    PostLogin(username, password)
                } else {
                    null
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
        post("/oauth/auth") {
            val postLogin = call.receive<PostLogin>()
            print(postLogin)
            if(postLogin != null) {
                val token = JwtConfig.generateToken(postLogin)
                call.respond(JsonRensponse(200, mapOf("access_token" to token, "expires_at" to "")))
            } else {
                call.respond(JsonRensponse(400, mapOf("message" to "missing parameters"), "error"))
            }
        }
        authenticate {
            route("/oauth/me") {
                handle {
                    val principal = call.authentication.principal<JWTPrincipal>()
                    val subjectString = principal!!.payload.subject.removePrefix("auth0|")
                    call.respondText("Success, $subjectString")
                }
            }
        }
    }
}

data class JsonRensponse<T>(val status: Short = 200, val data: T, val message: String = "Success")

