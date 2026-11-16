package ru.ifmo.se.api.infrastructure.security

import org.springframework.stereotype.Component
import ru.ifmo.se.api.application.port.out.TokenHasher
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

@Component
class Sha256TokenHasher : TokenHasher {
    override fun hash(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.toByteArray(StandardCharsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
