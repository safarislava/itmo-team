package ru.ifmo.se.api.domain.exception

class SubmissionNotFoundException(
    message: String = "Submission not found.",
) : DomainNotFoundException(message)

class SubmissionBadRequestException(
    message: String = "Bad request.",
) : DomainBadRequestException(message)

class SubmissionPayloadTooLargeException(
    message: String = "Source code exceeds the size limit of 2 MB.",
) : DomainPayloadTooLargeException(message)

class SubmissionAttemptsExhaustedException(
    message: String = "The attempts for the task are exhausted.",
) : DomainConflictException(message)
