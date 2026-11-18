package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.ApplicationId
import ru.ifmo.se.api.domain.model.Interview
import ru.ifmo.se.api.domain.model.InterviewFilter
import ru.ifmo.se.api.domain.model.InterviewId
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.VacancyId
import java.time.Instant

interface InterviewRepository {
    fun findById(id: InterviewId): Interview?

    fun findAll(
        filter: InterviewFilter,
        pageQuery: PageQuery,
    ): PageResult<Interview>

    fun save(interview: Interview): Interview

    fun scheduleInterview(
        applicationId: ApplicationId,
        scheduledStart: Instant,
        scheduledEnd: Instant,
    )

    fun findLatestScheduled(
        candidateId: PersonId,
        vacancyId: VacancyId,
        scheduledStart: Instant,
    ): Interview?
}
