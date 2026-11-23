package ru.ifmo.se.api.domain.exception

class LanguageNotFoundException(
    message: String = "Language not found.",
) : DomainNotFoundException(message)

class LanguageBadRequestException(
    message: String = "Bad request.",
) : DomainBadRequestException(message)
