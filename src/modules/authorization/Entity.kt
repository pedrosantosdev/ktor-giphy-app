package io.pedro.santos.dev.modules.authorization

import io.ktor.auth.Principal

data class PostLogin(val username: String, val password: String): Principal