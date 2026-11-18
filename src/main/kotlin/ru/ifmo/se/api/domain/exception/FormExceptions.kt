package ru.ifmo.se.api.domain.exception

class FormNotFoundException(
    message: String = "Form not found.",
) : RuntimeException(message)

class FormBadRequestException(
    message: String = "Bad request.",
) : RuntimeException(message)
