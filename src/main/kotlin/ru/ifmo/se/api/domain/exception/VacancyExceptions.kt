package ru.ifmo.se.api.domain.exception

class VacancyNotFoundException(
    message: String = "Vacancy not found.",
) : DomainNotFoundException(message)

class VacancyBadRequestException(
    message: String = "Bad request.",
) : DomainBadRequestException(message)

class VacancyNotDraftException(
    message: String = "The vacancy is not a draft.",
) : DomainConflictException(message)

class VacancyClosedOrArchivedException(
    message: String = "The vacancy is closed or archived.",
) : DomainConflictException(message)

class VacancyInvalidTransitionException(
    message: String = "The transition is not allowed.",
) : DomainConflictException(message)

class VacancyNoContestAttachedException(
    message: String = "The vacancy has no contest attached.",
) : DomainConflictException(message)
