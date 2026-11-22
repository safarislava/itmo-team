package ru.ifmo.se.api.domain.model

import java.time.Instant

data class HiringApplicationsMetrics(
    val total: Int,
    val byStatus: Map<ApplicationStatus, Int>,
)

data class HiringStatistics(
    val from: Instant,
    val to: Instant,
    val openVacancies: Int,
    val closedVacancies: Int,
    val applications: HiringApplicationsMetrics,
)
