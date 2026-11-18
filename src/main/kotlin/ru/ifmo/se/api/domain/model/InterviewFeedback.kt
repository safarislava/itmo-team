package ru.ifmo.se.api.domain.model

data class InterviewFeedback(
    val id: InterviewFeedbackId,
    val interviewId: InterviewId,
    val review: String,
    val score: Short,
    val decision: InterviewDecision,
)
