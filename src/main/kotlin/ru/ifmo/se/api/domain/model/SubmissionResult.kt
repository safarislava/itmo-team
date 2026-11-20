package ru.ifmo.se.api.domain.model

data class SubmissionResult(
    val verdict: SubmissionVerdict?,
    val executionTime: Int?,
    val memoryUsed: Int?,
)
