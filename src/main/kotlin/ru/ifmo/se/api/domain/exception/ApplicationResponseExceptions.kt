package ru.ifmo.se.api.domain.exception

class ApplicationResponseNotFoundException(
    message: String = "Application response not found.",
) : RuntimeException(message)

class ApplicationNotInReviewStatusException(
    message: String = "The application is not in REVIEW status.",
) : RuntimeException(message)

class ApplicationMaxResponsesReachedException(
    message: String = "The application has already received the maximum number of responses.",
) : RuntimeException(message)

class ApplicationResponseAccessDeniedException(
    message: String = "Access denied: you can only modify or delete your own response.",
) : RuntimeException(message)
