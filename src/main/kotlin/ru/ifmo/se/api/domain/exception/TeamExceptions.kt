package ru.ifmo.se.api.domain.exception

class TeamNotFoundException(
    message: String = "Team not found.",
) : DomainNotFoundException(message)

class TeamBadRequestException(
    message: String = "Bad request.",
) : DomainBadRequestException(message)
