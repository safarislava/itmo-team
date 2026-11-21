package ru.ifmo.se.api.application.dto

import ru.ifmo.se.api.domain.model.SkillId
import ru.ifmo.se.api.domain.model.VacancyStatus

data class CreateVacancyCommand(
    val title: String,
    val description: String,
    val city: String?,
    val salary: Int?,
    val skillIds: List<SkillId>,
)

data class UpdateVacancyCommand(
    val title: String,
    val description: String,
    val city: String?,
    val salary: Int?,
    val skillIds: List<SkillId>,
)

data class UpdateVacancyStatusCommand(
    val status: VacancyStatus,
)
