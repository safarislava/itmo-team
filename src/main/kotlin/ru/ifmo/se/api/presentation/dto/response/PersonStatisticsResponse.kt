package ru.ifmo.se.api.presentation.dto.response

import ru.ifmo.se.api.domain.model.Granularity

data class PersonStatisticsResponse(
    val person: PersonSummaryResponse,
    val totals: ActivityMetricsResponse,
    val points: List<ProductivityPointResponse>,
    val granularity: Granularity,
)
