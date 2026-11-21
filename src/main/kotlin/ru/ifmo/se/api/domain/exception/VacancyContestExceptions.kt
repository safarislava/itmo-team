package ru.ifmo.se.api.domain.exception

class VacancyContestAlreadyStartedException(
    message: String = "The contest has already started.",
) : RuntimeException(message)

class VacancyContestNotFoundException(
    message: String = "Contest is not attached to this vacancy.",
) : RuntimeException(message)
