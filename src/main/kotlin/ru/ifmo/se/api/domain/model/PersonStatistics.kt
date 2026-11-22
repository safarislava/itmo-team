package ru.ifmo.se.api.domain.model

data class PersonStatistics(
    val person: Person,
    val totals: ActivityMetrics,
    val points: List<ProductivityPoint>,
    val granularity: Granularity,
)
