package ru.ifmo.se.api.domain.exception

class InterviewNotFoundException(
    message: String = "Interview not found.",
) : DomainNotFoundException(message)

class InterviewBadRequestException(
    message: String = "Bad request.",
) : DomainBadRequestException(message)

class InterviewRecordingNotFoundException(
    message: String = "Interview recording not available.",
) : DomainNotFoundException(message)
