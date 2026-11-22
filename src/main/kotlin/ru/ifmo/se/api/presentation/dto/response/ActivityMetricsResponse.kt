package ru.ifmo.se.api.presentation.dto.response

data class ActivityMetricsResponse(
    val countOpenedPr: Int,
    val countClosedPr: Int,
    val countReviewedPr: Int,
    val countCommits: Int,
)
