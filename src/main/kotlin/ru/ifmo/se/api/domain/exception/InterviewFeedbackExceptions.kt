package ru.ifmo.se.api.domain.exception

class InterviewFeedbackNotFoundException(
    message: String = "Interview feedback not found.",
) : RuntimeException(message)

class InterviewFeedbackBadRequestException(
    message: String = "Bad request.",
) : RuntimeException(message)
