package ru.ifmo.se.api.domain.exception

abstract class DomainException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)

abstract class DomainNotFoundException(
    message: String,
    cause: Throwable? = null,
) : DomainException(message, cause)

abstract class DomainBadRequestException(
    message: String,
    cause: Throwable? = null,
) : DomainException(message, cause)

abstract class DomainConflictException(
    message: String,
    cause: Throwable? = null,
) : DomainException(message, cause)

abstract class DomainForbiddenException(
    message: String,
    cause: Throwable? = null,
) : DomainException(message, cause)

abstract class DomainUnauthorizedException(
    message: String,
    cause: Throwable? = null,
) : DomainException(message, cause)

abstract class DomainPayloadTooLargeException(
    message: String,
    cause: Throwable? = null,
) : DomainException(message, cause)
