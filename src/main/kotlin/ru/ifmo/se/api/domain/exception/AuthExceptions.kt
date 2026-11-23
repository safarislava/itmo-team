package ru.ifmo.se.api.domain.exception

class InvalidCredentialsException(
    message: String = "The email or the password is wrong.",
) : DomainUnauthorizedException(message)

class InvalidRefreshTokenException(
    message: String = "The refresh token is missing, expired or revoked.",
) : DomainUnauthorizedException(message)
