package ru.ifmo.se.api.domain.model

data class SubmissionResponse(
    val verdict: SubmissionVerdict?,
    val executionTime: Int?,
    val memoryUsed: Int?,
)
