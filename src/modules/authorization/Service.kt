package io.pedro.santos.dev.modules.authorization

import io.pedro.santos.dev.AuthenticationException
import io.pedro.santos.dev.modules.user.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class AuthorizationService {

    suspend fun authenticate(login: PostLogin): JwtConfig.Token? {
        UserRepository().findByUsername(login.username)?.let { user ->
            if(!user.active && BCryptPasswordEncoder().matches(login.password, user.password)) {
                return JwtConfig.accessToken(user)
            } else {
                throw AuthenticationException("Wrong credentials")
            }
        } ?: throw AuthenticationException("Wrong credentials")
    }
}