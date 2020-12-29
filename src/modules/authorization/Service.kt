package io.pedro.santos.dev.modules.authorization

import io.pedro.santos.dev.modules.user.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class AuthorizationService {

    suspend fun authenticate(login: PostLogin): JwtConfig.Token? {
        val user = UserRepository().findByUsername(login.username)
        if(user != null && !user.active && BCryptPasswordEncoder().matches(login.password, user.password)) {
            return JwtConfig.accessToken(user)
        }
        return null
    }
}