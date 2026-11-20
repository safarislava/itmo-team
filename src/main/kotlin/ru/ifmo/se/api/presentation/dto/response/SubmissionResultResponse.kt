package ru.ifmo.se.api.presentation.dto.response

import ru.ifmo.se.api.domain.model.SubmissionVerdict

data class SubmissionResultResponse(
    val verdict: SubmissionVerdict?,
    val executionTime: Int?,
    val memoryUsed: Int?,
)
