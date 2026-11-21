package ru.ifmo.se.api.domain.exception

class TeamNotFoundException(
    message: String = "Team not found.",
) : RuntimeException(message)

class TeamBadRequestException(
    message: String = "Bad request.",
) : RuntimeException(message)
