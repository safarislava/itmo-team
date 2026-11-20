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

class CandidateNotParticipantException(
    message: String = "The candidate does not participate in this contest.",
) : RuntimeException(message)

class ContestNotRunningException(
    message: String = "The contest is not running.",
) : RuntimeException(message)
