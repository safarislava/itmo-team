package ru.ifmo.se.api.domain.exception

class InterviewFeedbackNotFoundException(
    message: String = "Interview feedback not found.",
) : DomainNotFoundException(message)

class InterviewFeedbackBadRequestException(
    message: String = "Bad request.",
) : DomainBadRequestException(message)
