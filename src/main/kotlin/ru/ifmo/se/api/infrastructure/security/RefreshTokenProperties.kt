package ru.ifmo.se.api.infrastructure.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.ifmo.se.api.application.port.out.RefreshTokenPolicy

@Component
class RefreshTokenProperties(
    @Value("\${security.refresh-token.expiration-seconds}")
    override val expirationSeconds: Long,
) : RefreshTokenPolicy
