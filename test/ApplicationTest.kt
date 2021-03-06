package io.pedro.santos.dev

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlin.test.*
import io.ktor.server.testing.*

class ApplicationTest {
    @Test
    fun testRoot() {
            withTestApplication({ module() }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("{\n" +
                        "  \"status\": 200,\n" +
                        "  \"data\": {\n" +
                        "    \"message\": \"API ACTIVE!\"\n" +
                        "  },\n" +
                        "  \"message\": \"Success\"\n" +
                        "}", response.content)
            }
        }
    }
}
