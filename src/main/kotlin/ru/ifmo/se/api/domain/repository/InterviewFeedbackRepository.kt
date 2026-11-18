package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.InterviewFeedback
import ru.ifmo.se.api.domain.model.InterviewId

interface InterviewFeedbackRepository {
    fun findByInterviewId(interviewId: InterviewId): InterviewFeedback?

    fun save(feedback: InterviewFeedback): InterviewFeedback
}
