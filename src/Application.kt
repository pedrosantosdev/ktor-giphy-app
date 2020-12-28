package io.pedro.santos.dev

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.auth.*
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import kotlinx.coroutines.*

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

    install(StatusPages) {
        exception<Throwable> { e ->
            call.respondText(e.localizedMessage,ContentType.Text.Plain, HttpStatusCode.InternalServerError)
        }
    }

    install(Authentication) {

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
            call.respondText("HELLO WORLD!", contentType = ContentType.Application.Json)
        }
        post("/oauth/auth") {
            call.respondText("LOGIN!", contentType = ContentType.Application.Json)
        }
    }
}

data class JsonSampleClass(val status: Short = 200,val data: HttpResponseData, val message: String = "Success")

