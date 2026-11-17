package ru.ifmo.se.api.domain.exception

class CompanyNotFoundException(
    message: String = "Company not found.",
) : RuntimeException(message)

class CompanyBadRequestException(
    message: String = "Bad request.",
) : RuntimeException(message)
