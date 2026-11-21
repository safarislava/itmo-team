package ru.ifmo.se.api.domain.exception

class SkillNotFoundException(
    message: String = "Skill not found.",
) : RuntimeException(message)

class SkillBadRequestException(
    message: String = "Bad request.",
) : RuntimeException(message)
