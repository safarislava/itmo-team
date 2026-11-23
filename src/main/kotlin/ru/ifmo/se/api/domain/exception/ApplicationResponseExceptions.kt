package ru.ifmo.se.api.domain.exception

class ApplicationResponseNotFoundException(
    message: String = "Application response not found.",
) : DomainNotFoundException(message)

class ApplicationNotInReviewStatusException(
    message: String = "The application is not in REVIEW status.",
) : DomainConflictException(message)

class ApplicationMaxResponsesReachedException(
    message: String = "The application has already received the maximum number of responses.",
) : DomainConflictException(message)

class ApplicationResponseAccessDeniedException(
    message: String = "Access denied: you can only modify or delete your own response.",
) : DomainForbiddenException(message)
