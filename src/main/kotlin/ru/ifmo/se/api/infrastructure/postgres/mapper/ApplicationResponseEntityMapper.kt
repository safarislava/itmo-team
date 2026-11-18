package ru.ifmo.se.api.infrastructure.postgres.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.exception.ApplicationNotFoundException
import ru.ifmo.se.api.domain.exception.PersonNotFoundException
import ru.ifmo.se.api.domain.model.ApplicationId
import ru.ifmo.se.api.domain.model.ApplicationResponse
import ru.ifmo.se.api.domain.model.ApplicationResponseId
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.infrastructure.postgres.entity.ApplicationResponseEntity
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaApplicationRepository
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaPersonRepository

@Component
class ApplicationResponseEntityMapper(
    private val applicationRepository: JpaApplicationRepository,
    private val personRepository: JpaPersonRepository,
) {
    fun toDomain(entity: ApplicationResponseEntity): ApplicationResponse =
        ApplicationResponse(
            id = ApplicationResponseId(entity.id),
            applicationId = ApplicationId(entity.application.id),
            hrId = PersonId(entity.hr.id),
            motivation = entity.motivation,
            softSkills = entity.softSkills,
            petProjects = entity.petProjects,
            jobExp = entity.jobExp,
            hardSkills = entity.hardSkills,
            workFormat = entity.workFormat,
        )

    fun toEntity(domain: ApplicationResponse): ApplicationResponseEntity {
        val appEntity =
            applicationRepository.findById(domain.applicationId.value).orElseThrow {
                ApplicationNotFoundException()
            }
        val hrEntity =
            personRepository.findById(domain.hrId.value).orElseThrow {
                PersonNotFoundException()
            }
        return ApplicationResponseEntity(
            id = domain.id.value,
            application = appEntity,
            hr = hrEntity,
            motivation = domain.motivation,
            softSkills = domain.softSkills,
            petProjects = domain.petProjects,
            jobExp = domain.jobExp,
            hardSkills = domain.hardSkills,
            workFormat = domain.workFormat,
        )
    }
}
