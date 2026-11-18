package ru.ifmo.se.api.infrastructure.postgres.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.Interview
import ru.ifmo.se.api.domain.model.InterviewId
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.VacancyId
import ru.ifmo.se.api.infrastructure.postgres.entity.InterviewEntity

@Component
class InterviewEntityMapper {
    fun toDomain(entity: InterviewEntity): Interview =
        Interview(
            id = InterviewId(entity.id),
            candidateId = PersonId(entity.candidate.id),
            teamleadId = PersonId(entity.teamlead.id),
            vacancyId = VacancyId(entity.vacancy.id),
            videoS3key = entity.videoS3key,
            scheduledStart = entity.scheduledStart,
            scheduledEnd = entity.scheduledEnd,
            status = entity.status,
        )
}
