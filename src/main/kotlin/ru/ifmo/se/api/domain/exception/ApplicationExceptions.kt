package ru.ifmo.se.api.domain.exception

class ApplicationNotFoundException(
    message: String = "Application not found.",
) : RuntimeException(message)

class ApplicationBadRequestException(
    message: String = "Bad request.",
) : RuntimeException(message)
