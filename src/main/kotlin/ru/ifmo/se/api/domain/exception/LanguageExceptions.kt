package ru.ifmo.se.api.domain.exception

class LanguageNotFoundException(
    message: String = "Language not found.",
) : RuntimeException(message)

class LanguageBadRequestException(
    message: String = "Bad request.",
) : RuntimeException(message)
