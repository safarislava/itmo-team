package ru.ifmo.se.api.domain.exception

class SkillNotFoundException(
    message: String = "Skill not found.",
) : DomainNotFoundException(message)

class SkillBadRequestException(
    message: String = "Bad request.",
) : DomainBadRequestException(message)
