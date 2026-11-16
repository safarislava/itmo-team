package ru.ifmo.se.api.infrastructure.security

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import ru.ifmo.se.api.application.port.out.PasswordHasher

@Component
class BCryptPasswordHasher(
    private val passwordEncoder: PasswordEncoder,
) : PasswordHasher {
    override fun hash(rawPassword: String): String = checkNotNull(passwordEncoder.encode(rawPassword))

    override fun matches(
        rawPassword: String,
        hashedPassword: String,
    ): Boolean = passwordEncoder.matches(rawPassword, hashedPassword)
}
