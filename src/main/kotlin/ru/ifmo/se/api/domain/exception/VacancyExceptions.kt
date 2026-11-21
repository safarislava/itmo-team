package ru.ifmo.se.api.domain.exception

class VacancyNotFoundException(
    message: String = "Vacancy not found.",
) : RuntimeException(message)

class VacancyBadRequestException(
    message: String = "Bad request.",
) : RuntimeException(message)

class VacancyNotDraftException(
    message: String = "The vacancy is not a draft.",
) : RuntimeException(message)

class VacancyClosedOrArchivedException(
    message: String = "The vacancy is closed or archived.",
) : RuntimeException(message)

class VacancyInvalidTransitionException(
    message: String = "The transition is not allowed.",
) : RuntimeException(message)

class VacancyNoContestAttachedException(
    message: String = "The vacancy has no contest attached.",
) : RuntimeException(message)
