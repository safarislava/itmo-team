package ru.ifmo.se.api.presentation.dto.request

import ru.ifmo.se.api.domain.model.VacancyStatus

data class VacancyStatusRequest(
    val status: VacancyStatus,
)
