package ru.ifmo.se.api.domain.exception

class FormNotFoundException(
    message: String = "Form not found.",
) : DomainNotFoundException(message)

class FormBadRequestException(
    message: String = "Bad request.",
) : DomainBadRequestException(message)

class FormLockedException(
    message: String = "Form cannot be updated because it is linked to an application under review.",
) : DomainConflictException(message)
