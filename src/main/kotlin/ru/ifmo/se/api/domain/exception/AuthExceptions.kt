package ru.ifmo.se.api.domain.exception

class EmailAlreadyExistsException(
    message: String = "A user with this email already exists.",
) : RuntimeException(message)

class InvalidCredentialsException(
    message: String = "The email or the password is wrong.",
) : RuntimeException(message)

class InvalidRefreshTokenException(
    message: String = "The refresh token is missing, expired or revoked.",
) : RuntimeException(message)
