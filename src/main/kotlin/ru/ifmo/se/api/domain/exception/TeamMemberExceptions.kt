package ru.ifmo.se.api.domain.exception

class InvalidTeamMemberException(
    message: String = "The person is a candidate, belongs to another company or leads another team.",
) : RuntimeException(message)

class TeamLeadCannotBeRemovedException(
    message: String = "The person is the team lead of the team.",
) : RuntimeException(message)
