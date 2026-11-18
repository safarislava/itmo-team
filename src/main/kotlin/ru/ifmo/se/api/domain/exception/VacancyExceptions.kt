package ru.ifmo.se.api.domain.exception

class VacancyNotFoundException(
    message: String = "Vacancy not found.",
) : RuntimeException(message)

class VacancyBadRequestException(
    message: String = "Bad request.",
) : RuntimeException(message)
