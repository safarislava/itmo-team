package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.InterviewFeedbackEntity
import java.util.UUID

interface JpaInterviewFeedbackRepository : JpaRepository<InterviewFeedbackEntity, UUID> {
    fun findByInterviewId(interviewId: UUID): InterviewFeedbackEntity?
}
