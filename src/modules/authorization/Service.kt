package io.pedro.santos.dev.modules.authorization

import io.ktor.auth.*
import io.pedro.santos.dev.AuthenticationException
import io.pedro.santos.dev.AuthorizationException
import io.pedro.santos.dev.BadRequestException
import io.pedro.santos.dev.modules.user.User
import io.pedro.santos.dev.modules.user.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class AuthorizationService {

    suspend fun authenticate(login: PostLogin): JwtConfig.Token {
        UserRepository().findByUsername(login.username)?.let { user ->
            if(!user.active) throw AuthenticationException("User Inactivated, Contact App Administrator")
            if(user.active && BCryptPasswordEncoder().matches(login.password, user.password))
                return JwtConfig.accessToken(user)
            else throw AuthenticationException("Wrong credentials")
        } ?: throw AuthenticationException("Wrong credentials")
    }

    suspend fun register(entity: PostLogin): Map<String, String>{
        val user = User(username = entity.username, password = entity.password, active = false)
        UserRepository().create(user)?.let {
            return mapOf("message" to "User Register Success")
        } ?: throw BadRequestException("Error In Register")
    }

    suspend fun refreshToken(refreshToken: String): JwtConfig.Token {
        JwtConfig.verifyToken(refreshToken)?.let { id ->
            UserRepository().findById(id)?.let { user ->
                return JwtConfig.accessToken(user)
            } ?: throw AuthorizationException()
        } ?: throw AuthorizationException()
    }
}