package ru.ifmo.se.api.presentation.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.InterviewFeedback
import ru.ifmo.se.api.presentation.dto.response.InterviewFeedbackResponse

@Component
class InterviewFeedbackResponseMapper {
    fun toResponse(feedback: InterviewFeedback): InterviewFeedbackResponse =
        InterviewFeedbackResponse(
            id = feedback.id.value,
            review = feedback.review,
            score = feedback.score,
            decision = feedback.decision,
        )
}
