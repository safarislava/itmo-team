package ru.ifmo.se.api.infrastructure.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.ifmo.se.api.application.port.out.AccessTokenPolicy

@Component
class AccessTokenProperties(
    @Value("\${security.access-token.expiration-seconds}")
    override val expirationSeconds: Long,
) : AccessTokenPolicy
