package ru.ifmo.se.api.infrastructure.postgres.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.InterviewFeedback
import ru.ifmo.se.api.domain.model.InterviewFeedbackId
import ru.ifmo.se.api.domain.model.InterviewId
import ru.ifmo.se.api.infrastructure.postgres.entity.InterviewFeedbackEntity

@Component
class InterviewFeedbackEntityMapper {
    fun toDomain(entity: InterviewFeedbackEntity): InterviewFeedback =
        InterviewFeedback(
            id = InterviewFeedbackId(entity.id),
            interviewId = InterviewId(entity.interview.id),
            review = entity.review,
            score = entity.score,
            decision = entity.decision,
        )
}
