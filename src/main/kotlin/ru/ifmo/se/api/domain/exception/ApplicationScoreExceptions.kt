package ru.ifmo.se.api.domain.exception

class ApplicationScoreExceptions(
    message: String = "Application score not found.",
) : RuntimeException(message)
