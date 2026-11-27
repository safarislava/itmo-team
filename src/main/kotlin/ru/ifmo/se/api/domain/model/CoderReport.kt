package ru.ifmo.se.api.domain.model

import java.time.LocalDate

data class CoderReport(
    val id: CoderReportId,
    val coderId: PersonId?,
    val metrics: ActivityMetrics,
    val reportDate: LocalDate,
)
