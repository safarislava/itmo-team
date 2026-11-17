package ru.ifmo.se.api.domain.exception

class PersonNotFoundException(
    message: String = "Person not found.",
) : RuntimeException(message)

class PersonBadRequestException(
    message: String = "Bad request.",
) : RuntimeException(message)

class InvalidCurrentPasswordException(
    message: String = "The current password is wrong or the new one failed validation.",
) : RuntimeException(message)
