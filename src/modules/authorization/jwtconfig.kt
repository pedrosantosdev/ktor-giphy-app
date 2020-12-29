package io.pedro.santos.dev.modules.authorization

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.pedro.santos.dev.modules.authorization.PostLogin
import java.util.*

object JwtConfig {
    private const val secret = application.environment.config
        .propertyOrNull("jwt.secret").getString()
    private const val issuer = environment.config.property("jwt.domain").getString()
    private const val audience = environment.config.property("jwt.audience").getString()
    private const val validityInMs = 36_000_00 * 24 // 1 day
    private val algorithm = Algorithm.HMAC512(secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .withAudience(audience)
        .withIssuer(issuer)
        .build()

    /**
     * Produce a token for this combination of username and password
     */
    fun generateToken(user: PostLogin): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaim("username", user.username)
        .withClaim("password", user.password)
        .withExpiresAt(getExpiration())  // optional
        .sign(algorithm)

    /**
     * Calculate the expiration Date based on current time + the given validity
     */
    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)

}