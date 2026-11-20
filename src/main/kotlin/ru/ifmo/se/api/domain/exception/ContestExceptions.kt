package ru.ifmo.se.api.domain.exception

class ContestNotFoundException(
    message: String = "Contest not found.",
) : RuntimeException(message)

class ContestBadRequestException(
    message: String = "Bad request.",
) : RuntimeException(message)

class ContestAlreadyStartedException(
    message: String = "The contest has already started.",
) : RuntimeException(message)
