package ru.ifmo.se.api.domain.exception

class SubmissionNotFoundException(
    message: String = "Submission not found.",
) : RuntimeException(message)

class SubmissionBadRequestException(
    message: String = "Bad request.",
) : RuntimeException(message)

class SubmissionPayloadTooLargeException(
    message: String = "Source code exceeds the size limit of 2 MB.",
) : RuntimeException(message)

class SubmissionAttemptsExhaustedException(
    message: String = "The attempts for the task are exhausted.",
) : RuntimeException(message)
