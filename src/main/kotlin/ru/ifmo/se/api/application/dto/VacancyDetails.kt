package ru.ifmo.se.api.application.dto

import ru.ifmo.se.api.domain.model.Company
import ru.ifmo.se.api.domain.model.Skill
import ru.ifmo.se.api.domain.model.Team
import ru.ifmo.se.api.domain.model.Vacancy

data class VacancyDetails(
    val vacancy: Vacancy,
    val team: Team,
    val company: Company,
    val skills: List<Skill>,
)
