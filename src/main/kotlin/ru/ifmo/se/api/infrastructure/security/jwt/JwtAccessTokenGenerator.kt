package ru.ifmo.se.api.infrastructure.security.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.ifmo.se.api.application.port.out.AccessTokenGenerator
import ru.ifmo.se.api.application.port.out.AccessTokenPolicy
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonId
import java.nio.charset.StandardCharsets
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey

@Component
class JwtAccessTokenGenerator(
    @Value("\${security.jwt.secret}") secret: String,
    private val accessTokenPolicy: AccessTokenPolicy,
) : AccessTokenGenerator {
    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    override fun generate(person: Person): String {
        val now = Date()
        val expiry = Date(now.time + accessTokenPolicy.expirationSeconds * 1000)
        return Jwts
            .builder()
            .subject(person.id.value.toString())
            .claim("email", person.email)
            .claim("role", person.role.name)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()
    }

    fun parseClaims(token: String): Claims? =
        try {
            Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (_: Exception) {
            null
        }

    fun validateAndGetPersonId(token: String): UUID? = parseClaims(token)?.subject?.let { PersonId.fromOrNull(it)?.value }
}
