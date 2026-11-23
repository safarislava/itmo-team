package ru.ifmo.se.api.domain.exception

class CompanyNotFoundException(
    message: String = "Company not found.",
) : DomainNotFoundException(message)

class CompanyBadRequestException(
    message: String = "Bad request.",
) : DomainBadRequestException(message)
