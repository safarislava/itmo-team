package ru.ifmo.se.api.domain.exception

class ContestNotFoundException(
    message: String = "Contest not found.",
) : DomainNotFoundException(message)

class ContestBadRequestException(
    message: String = "Bad request.",
) : DomainBadRequestException(message)

class ContestAlreadyStartedException(
    message: String = "The contest has already started.",
) : DomainConflictException(message)

class CandidateNotParticipantException(
    message: String = "The candidate does not participate in this contest.",
) : DomainForbiddenException(message)

class ContestNotRunningException(
    message: String = "The contest is not running.",
) : DomainConflictException(message)
