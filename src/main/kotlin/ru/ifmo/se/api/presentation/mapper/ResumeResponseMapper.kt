package ru.ifmo.se.api.presentation.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.Resume
import ru.ifmo.se.api.presentation.dto.response.ResumeResponse

@Component
class ResumeResponseMapper {
    fun toResponse(resume: Resume): ResumeResponse =
        ResumeResponse(
            id = resume.id.value,
            filename = resume.filename,
        )
}
