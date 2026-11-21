package ru.ifmo.se.api.application.dto

import ru.ifmo.se.api.domain.model.Company
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.Team

data class TeamDetails(
    val team: Team,
    val company: Company,
    val teamlead: Person,
)
