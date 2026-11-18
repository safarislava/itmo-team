package ru.ifmo.se.api.presentation.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.ifmo.se.api.application.dto.SaveInterviewFeedbackCommand
import ru.ifmo.se.api.application.service.InterviewFeedbackService
import ru.ifmo.se.api.domain.model.InterviewId
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.infrastructure.security.annotation.RequiresRole
import ru.ifmo.se.api.presentation.dto.request.InterviewFeedbackRequest
import ru.ifmo.se.api.presentation.dto.response.InterviewFeedbackResponse
import ru.ifmo.se.api.presentation.mapper.InterviewFeedbackResponseMapper
import java.util.UUID

@Validated
@RestController
@RequestMapping("/api/interviews")
class InterviewFeedbackController(
    private val interviewFeedbackService: InterviewFeedbackService,
    private val interviewFeedbackResponseMapper: InterviewFeedbackResponseMapper,
) {
    @GetMapping("/{interviewId}/feedback")
    @RequiresRole(
        PersonRole.HR,
        PersonRole.TEAM_LEAD,
        PersonRole.MANAGER,
        PersonRole.ADMIN,
    )
    fun getInterviewFeedback(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable interviewId: UUID,
    ): ResponseEntity<InterviewFeedbackResponse> {
        val feedback = interviewFeedbackService.getInterviewFeedback(currentPerson, InterviewId(interviewId))
        return ResponseEntity.ok(interviewFeedbackResponseMapper.toResponse(feedback))
    }

    @PutMapping("/{interviewId}/feedback")
    @RequiresRole(PersonRole.TEAM_LEAD, PersonRole.ADMIN)
    fun saveInterviewFeedback(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable interviewId: UUID,
        @Valid @RequestBody request: InterviewFeedbackRequest,
    ): ResponseEntity<InterviewFeedbackResponse> {
        val command =
            SaveInterviewFeedbackCommand(
                review = request.review,
                score = request.score,
                decision = request.decision,
            )
        val saved = interviewFeedbackService.saveInterviewFeedback(currentPerson, InterviewId(interviewId), command)
        return ResponseEntity.ok(interviewFeedbackResponseMapper.toResponse(saved))
    }
}
