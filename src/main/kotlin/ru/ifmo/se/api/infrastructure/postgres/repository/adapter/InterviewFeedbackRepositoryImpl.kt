package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import org.springframework.stereotype.Repository
import ru.ifmo.se.api.domain.model.InterviewFeedback
import ru.ifmo.se.api.domain.model.InterviewId
import ru.ifmo.se.api.domain.repository.InterviewFeedbackRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.InterviewFeedbackEntity
import ru.ifmo.se.api.infrastructure.postgres.mapper.InterviewFeedbackEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaInterviewFeedbackRepository
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaInterviewRepository

@Repository
class InterviewFeedbackRepositoryImpl(
    private val repo: JpaInterviewFeedbackRepository,
    private val interviewRepo: JpaInterviewRepository,
    private val mapper: InterviewFeedbackEntityMapper,
) : InterviewFeedbackRepository {
    override fun findByInterviewId(interviewId: InterviewId): InterviewFeedback? =
        repo.findByInterviewId(interviewId.value)?.let(mapper::toDomain)

    override fun save(feedback: InterviewFeedback): InterviewFeedback {
        val existing = repo.findByInterviewId(feedback.interviewId.value)
        val entity =
            if (existing != null) {
                existing.review = feedback.review
                existing.score = feedback.score
                existing.decision = feedback.decision
                existing
            } else {
                val interview = interviewRepo.findById(feedback.interviewId.value).orElseThrow()
                InterviewFeedbackEntity(
                    id = feedback.id.value,
                    interview = interview,
                    review = feedback.review,
                    score = feedback.score,
                    decision = feedback.decision,
                )
            }
        return mapper.toDomain(repo.save(entity))
    }
}
