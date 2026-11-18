package ru.ifmo.se.api.domain.exception

class InterviewNotFoundException(
    message: String = "Interview not found.",
) : RuntimeException(message)

class InterviewBadRequestException(
    message: String = "Bad request.",
) : RuntimeException(message)

class InterviewRecordingNotFoundException(
    message: String = "Interview recording not available.",
) : RuntimeException(message)
