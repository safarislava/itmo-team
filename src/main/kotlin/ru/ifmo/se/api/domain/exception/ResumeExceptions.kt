package ru.ifmo.se.api.domain.exception

class ResumeNotFoundException(
    message: String = "Resume not found.",
) : DomainNotFoundException(message)

class ResumeBadRequestException(
    message: String = "Bad request.",
) : DomainBadRequestException(message)

class ResumeLockedException(
    message: String = "Resume cannot be updated because it is linked to an application under review.",
) : DomainConflictException(message)
