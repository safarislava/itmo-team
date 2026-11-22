package ru.ifmo.se.api.presentation.dto.response

import java.time.LocalDate

data class ProductivityPointResponse(
    val periodStart: LocalDate,
    val countOpenedPr: Int,
    val countClosedPr: Int,
    val countReviewedPr: Int,
    val countCommits: Int,
)
