package ru.ifmo.se.api.application.dto

import ru.ifmo.se.api.domain.model.ApplicationId

data class VacancyStatusResultDto(
    val details: VacancyDetails,
    val rejectedApplicationIds: List<ApplicationId>,
)
