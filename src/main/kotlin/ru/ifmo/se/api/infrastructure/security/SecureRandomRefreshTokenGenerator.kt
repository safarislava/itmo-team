package ru.ifmo.se.api.infrastructure.security

import org.springframework.stereotype.Component
import ru.ifmo.se.api.application.port.out.RefreshTokenGenerator
import java.security.SecureRandom
import java.util.Base64

@Component
class SecureRandomRefreshTokenGenerator : RefreshTokenGenerator {
    private val secureRandom = SecureRandom()

    override fun generate(): String {
        val randomBytes = ByteArray(32)
        secureRandom.nextBytes(randomBytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes)
    }
}
