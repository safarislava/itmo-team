package ru.ifmo.se.api.domain.exception

class AnalyticsBadRequestException(
    message: String = "Bad request.",
) : RuntimeException(message)

class AnalyticsNotFoundException(
    message: String = "Resource not found.",
) : RuntimeException(message)
