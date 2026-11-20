package ru.ifmo.se.api.domain.exception

class ResumeNotFoundException(
    message: String = "Resume not found.",
) : RuntimeException(message)

class ResumeBadRequestException(
    message: String = "Bad request.",
) : RuntimeException(message)

class ResumeLockedException(
    message: String = "Resume cannot be updated because it is linked to an application under review.",
) : RuntimeException(message)
