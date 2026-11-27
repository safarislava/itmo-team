package ru.ifmo.se.api.domain.model

import java.time.LocalDate

data class HiringApplicationsMetrics(
    val total: Int,
    val byStatus: Map<ApplicationStatus, Int>,
)

data class HiringStatistics(
    val from: LocalDate,
    val to: LocalDate,
    val openVacancies: Int,
    val closedVacancies: Int,
    val applications: HiringApplicationsMetrics,
)
