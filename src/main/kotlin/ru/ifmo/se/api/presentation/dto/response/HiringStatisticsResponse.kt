package ru.ifmo.se.api.presentation.dto.response

import java.time.Instant

data class HiringStatisticsResponse(
    val from: Instant,
    val to: Instant,
    val openVacancies: Int,
    val closedVacancies: Int,
    val applications: HiringApplicationsResponse,
)
