package io.pedro.santos.dev.modules.authorization

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.typesafe.config.ConfigFactory
import io.ktor.config.*
import io.pedro.santos.dev.modules.user.User
import java.util.*

object JwtConfig {
    private val appConfig = HoconApplicationConfig(ConfigFactory.load())
    private val secret = appConfig.property("jwt.secret").toString()
    private val issuer = appConfig.property("jwt.domain").toString()
    private val audience = appConfig.property("jwt.audience").toString()
    val realm = appConfig.property("jwt.realm").toString()
    private const val validityInMs = 36_000_00 * 24 // 1 day
    private val algorithm = Algorithm.HMAC512(secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .withAudience(audience)
        .build()

    /**
     * Produce a token for this combination of username and password
     */
    private fun generateToken(user: User): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withAudience(audience)
        .withClaim("username", user.username)
        .withExpiresAt(getExpiration())  // optional
        .sign(algorithm)

    fun accessToken(user: User): Token = Token(generateToken(user), getExpiration().toString())

    /**
     * Calculate the expiration Date based on current time + the given validity
     */
    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)

    data class Token(val access_token: String, val expires_at: String)

}