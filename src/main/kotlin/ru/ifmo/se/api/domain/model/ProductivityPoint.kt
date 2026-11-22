package ru.ifmo.se.api.domain.model

import java.time.LocalDate

data class ProductivityPoint(
    val periodStart: LocalDate,
    val metrics: ActivityMetrics,
)
