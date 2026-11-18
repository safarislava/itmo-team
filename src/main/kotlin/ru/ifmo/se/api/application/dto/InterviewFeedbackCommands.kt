@file:Suppress("ktlint:standard:filename")

package ru.ifmo.se.api.application.dto

import ru.ifmo.se.api.domain.model.InterviewDecision

data class SaveInterviewFeedbackCommand(
    val review: String,
    val score: Short,
    val decision: InterviewDecision,
)
