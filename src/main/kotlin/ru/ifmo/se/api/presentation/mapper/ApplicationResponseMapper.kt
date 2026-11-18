package ru.ifmo.se.api.presentation.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.application.dto.ApplicationDetails
import ru.ifmo.se.api.presentation.dto.response.ApplicationResponse

@Component
class ApplicationResponseMapper(
    private val formResponseMapper: FormResponseMapper,
    private val resumeResponseMapper: ResumeResponseMapper,
) {
    fun toResponse(details: ApplicationDetails): ApplicationResponse =
        ApplicationResponse(
            id = details.application.id.value,
            candidateId = details.application.candidateId.value,
            vacancyId = details.application.vacancyId.value,
            resume = resumeResponseMapper.toResponse(details.resume),
            form = formResponseMapper.toResponse(details.form),
            status = details.application.status,
        )
}
