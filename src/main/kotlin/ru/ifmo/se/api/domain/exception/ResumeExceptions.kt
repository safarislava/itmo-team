package ru.ifmo.se.api.domain.exception

class ResumeNotFoundException(
    message: String = "Resume not found.",
) : RuntimeException(message)

class ResumeBadRequestException(
    message: String = "Bad request.",
) : RuntimeException(message)
