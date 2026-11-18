package ru.ifmo.se.api.presentation.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.Form
import ru.ifmo.se.api.presentation.dto.response.FormResponse

@Component
class FormResponseMapper {
    fun toResponse(form: Form): FormResponse =
        FormResponse(
            id = form.id.value,
            university = form.university,
            motivation = form.motivation,
            petProjects = form.petProjects,
            jobExp = form.jobExp,
            workFormat = form.workFormat,
            skillIds = form.skillIds,
        )
}
