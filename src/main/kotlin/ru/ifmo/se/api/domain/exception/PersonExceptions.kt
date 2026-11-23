package ru.ifmo.se.api.domain.exception

class PersonNotFoundException(
    message: String = "Person not found.",
) : DomainNotFoundException(message)

class PersonBadRequestException(
    message: String = "Bad request.",
) : DomainBadRequestException(message)

class InvalidCurrentPasswordException(
    message: String = "The current password is wrong or the new one failed validation.",
) : DomainBadRequestException(message)
