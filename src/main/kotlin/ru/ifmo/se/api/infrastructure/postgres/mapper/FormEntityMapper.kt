package ru.ifmo.se.api.infrastructure.postgres.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.Form
import ru.ifmo.se.api.domain.model.FormId
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.infrastructure.postgres.entity.FormEntity

@Component
class FormEntityMapper {
    fun toDomain(entity: FormEntity): Form =
        Form(
            id = FormId(entity.id),
            candidateId = PersonId(entity.candidateId),
            university = entity.university,
            motivation = entity.motivation,
            petProjects = entity.petProjects,
            jobExp = entity.jobExp,
            workFormat = entity.workFormat,
            skillIds = entity.skillIds ?: emptyList(),
        )

    fun toEntity(form: Form): FormEntity =
        FormEntity(
            id = form.id.value,
            candidateId = form.candidateId.value,
            university = form.university,
            motivation = form.motivation,
            petProjects = form.petProjects,
            jobExp = form.jobExp,
            workFormat = form.workFormat,
            skillIds = form.skillIds,
        )
}
