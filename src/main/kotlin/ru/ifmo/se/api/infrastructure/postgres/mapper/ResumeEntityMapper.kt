package ru.ifmo.se.api.infrastructure.postgres.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.Resume
import ru.ifmo.se.api.domain.model.ResumeId
import ru.ifmo.se.api.infrastructure.postgres.entity.ResumeEntity

@Component
class ResumeEntityMapper {
    fun toDomain(entity: ResumeEntity): Resume =
        Resume(
            id = ResumeId(entity.id),
            candidateId = PersonId(entity.candidateId),
            s3key = entity.s3key,
            filename = entity.filename,
        )

    fun toEntity(resume: Resume): ResumeEntity =
        ResumeEntity(
            id = resume.id.value,
            candidateId = resume.candidateId.value,
            s3key = resume.s3key,
            filename = resume.filename,
        )
}
