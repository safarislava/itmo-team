package ru.ifmo.se.api.domain.exception

class AnalyticsBadRequestException(
    message: String = "Bad request.",
) : DomainBadRequestException(message)

class AnalyticsNotFoundException(
    message: String = "Resource not found.",
) : DomainNotFoundException(message)
