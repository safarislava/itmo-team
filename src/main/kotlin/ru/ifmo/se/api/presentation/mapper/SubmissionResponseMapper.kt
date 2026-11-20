package ru.ifmo.se.api.presentation.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.application.dto.SubmissionDetails
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.presentation.dto.response.SubmissionResponse
import ru.ifmo.se.api.presentation.dto.response.SubmissionResultResponse

@Component
class SubmissionResponseMapper(
    private val personResponseMapper: PersonResponseMapper,
    private val languageResponseMapper: LanguageResponseMapper,
) {
    fun toResponse(
        details: SubmissionDetails,
        caller: Person,
    ): SubmissionResponse =
        SubmissionResponse(
            id = details.submission.id.value,
            sender = personResponseMapper.toSummaryResponse(details.sender),
            contestId = details.submission.contestId.value,
            taskId = details.submission.taskId.value,
            language = languageResponseMapper.toResponse(details.language),
            status = details.submission.status,
            attempt = details.submission.attempt,
            aiScore = if (caller.role == PersonRole.CANDIDATE) null else details.submission.aiScore,
            submittedAt = details.submission.submittedAt,
            result =
                details.submission.response?.let {
                    SubmissionResultResponse(
                        verdict = it.verdict,
                        executionTime = it.executionTime,
                        memoryUsed = it.memoryUsed,
                    )
                },
        )
}
