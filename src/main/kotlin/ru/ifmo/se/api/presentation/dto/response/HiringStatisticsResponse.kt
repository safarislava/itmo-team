package ru.ifmo.se.api.presentation.dto.response

import java.time.LocalDate

data class HiringStatisticsResponse(
    val from: LocalDate,
    val to: LocalDate,
    val openVacancies: Int,
    val closedVacancies: Int,
    val applications: HiringApplicationsResponse,
)
