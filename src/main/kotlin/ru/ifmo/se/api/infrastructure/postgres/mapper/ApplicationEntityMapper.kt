package ru.ifmo.se.api.infrastructure.postgres.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.Application
import ru.ifmo.se.api.domain.model.ApplicationId
import ru.ifmo.se.api.domain.model.FormId
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.ResumeId
import ru.ifmo.se.api.domain.model.VacancyId
import ru.ifmo.se.api.infrastructure.postgres.entity.ApplicationEntity

@Component
class ApplicationEntityMapper {
    fun toDomain(entity: ApplicationEntity): Application =
        Application(
            id = ApplicationId(entity.id),
            candidateId = PersonId(entity.candidate.id),
            vacancyId = VacancyId(entity.vacancy.id),
            resumeId = ResumeId(entity.resume.id),
            formId = FormId(entity.form.id),
            status = entity.status,
        )
}
