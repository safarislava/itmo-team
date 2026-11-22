package ru.ifmo.se.api.infrastructure.postgres.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.ApplicationId
import ru.ifmo.se.api.domain.model.ApplicationScore
import ru.ifmo.se.api.infrastructure.postgres.entity.ApplicationScoreEntity

@Component
class ApplicationScoreEntityMapper {
    fun toDomain(entity: ApplicationScoreEntity): ApplicationScore =
        ApplicationScore(
            applicationId = ApplicationId(entity.application.id),
            motivation = entity.motivation,
            softSkills = entity.softSkills,
            petProjects = entity.petProjects,
            jobExp = entity.jobExp,
            hardSkills = entity.hardSkills,
            workFormat = entity.workFormat,
        )
}
