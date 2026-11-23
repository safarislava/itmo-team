package ru.ifmo.se.api.domain.exception

class ApplicationNotFoundException(
    message: String = "Application not found.",
) : DomainNotFoundException(message)

class ApplicationBadRequestException(
    message: String = "Bad request.",
) : DomainBadRequestException(message)
