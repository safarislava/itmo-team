package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.domain.model.ApplicationId
import ru.ifmo.se.api.domain.model.Interview
import ru.ifmo.se.api.domain.model.InterviewFilter
import ru.ifmo.se.api.domain.model.InterviewId
import ru.ifmo.se.api.domain.model.InterviewStatus
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.VacancyId
import ru.ifmo.se.api.domain.repository.InterviewRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.CompanyEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.InterviewEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.PersonEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.TeamEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.VacancyEntity
import ru.ifmo.se.api.infrastructure.postgres.extension.toPageRequest
import ru.ifmo.se.api.infrastructure.postgres.extension.toPageResult
import ru.ifmo.se.api.infrastructure.postgres.mapper.InterviewEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaApplicationRepository
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaInterviewRepository
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaPersonRepository
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaVacancyRepository
import java.time.Instant
import java.util.UUID

@Repository
class InterviewRepositoryImpl(
    private val repo: JpaInterviewRepository,
    private val mapper: InterviewEntityMapper,
    private val personRepo: JpaPersonRepository,
    private val vacancyRepo: JpaVacancyRepository,
    private val applicationRepo: JpaApplicationRepository,
) : InterviewRepository {
    override fun findById(id: InterviewId): Interview? = repo.findById(id.value).map(mapper::toDomain).orElse(null)

    override fun findAll(
        filter: InterviewFilter,
        pageQuery: PageQuery,
    ): PageResult<Interview> {
        val specification =
            Specification<InterviewEntity> { root, _, cb ->
                val predicates = mutableListOf<Predicate>()

                filter.candidateId?.let {
                    predicates.add(cb.equal(root.get<PersonEntity>("candidate").get<UUID>("id"), it.value))
                }

                filter.teamleadId?.let {
                    predicates.add(cb.equal(root.get<PersonEntity>("teamlead").get<UUID>("id"), it.value))
                }

                filter.vacancyId?.let {
                    predicates.add(cb.equal(root.get<VacancyEntity>("vacancy").get<UUID>("id"), it.value))
                }

                filter.status?.let {
                    predicates.add(cb.equal(root.get<InterviewStatus>("status"), it))
                }

                filter.dateFrom?.let {
                    predicates.add(cb.greaterThanOrEqualTo(root.get<Instant>("scheduledStart"), it))
                }

                filter.dateTo?.let {
                    predicates.add(cb.lessThanOrEqualTo(root.get<Instant>("scheduledEnd"), it))
                }

                filter.companyId?.let {
                    predicates.add(
                        cb.equal(
                            root
                                .get<VacancyEntity>("vacancy")
                                .get<TeamEntity>("team")
                                .get<CompanyEntity>("company")
                                .get<UUID>("id"),
                            it.value,
                        ),
                    )
                }

                filter.applicationId?.let { appId ->
                    val app = applicationRepo.findById(appId.value).orElse(null)
                    if (app != null) {
                        predicates.add(cb.equal(root.get<PersonEntity>("candidate").get<UUID>("id"), app.candidate.id))
                        predicates.add(cb.equal(root.get<VacancyEntity>("vacancy").get<UUID>("id"), app.vacancy.id))
                    } else {
                        predicates.add(cb.disjunction())
                    }
                }

                cb.and(*predicates.toTypedArray())
            }

        val pageable = pageQuery.toPageRequest("scheduledStart")
        return repo.findAll(specification, pageable).toPageResult(mapper::toDomain)
    }

    override fun save(interview: Interview): Interview {
        val existing = repo.findById(interview.id.value).orElse(null)
        val entity =
            if (existing != null) {
                existing.status = interview.status
                existing.videoS3key = interview.videoS3key
                existing.scheduledStart = interview.scheduledStart
                existing.scheduledEnd = interview.scheduledEnd
                existing
            } else {
                val candidate = personRepo.findById(interview.candidateId.value).orElseThrow()
                val teamlead = personRepo.findById(interview.teamleadId.value).orElseThrow()
                val vacancy = vacancyRepo.findById(interview.vacancyId.value).orElseThrow()

                InterviewEntity(
                    id = interview.id.value,
                    candidate = candidate,
                    teamlead = teamlead,
                    vacancy = vacancy,
                    videoS3key = interview.videoS3key,
                    scheduledStart = interview.scheduledStart,
                    scheduledEnd = interview.scheduledEnd,
                    status = interview.status,
                )
            }
        return mapper.toDomain(repo.save(entity))
    }

    @Transactional
    override fun scheduleInterview(
        applicationId: ApplicationId,
        scheduledStart: Instant,
        scheduledEnd: Instant,
    ) {
        repo.scheduleInterview(
            applicationId = applicationId.value,
            scheduledStart = scheduledStart,
            scheduledEnd = scheduledEnd,
        )
    }

    override fun findLatestScheduled(
        candidateId: PersonId,
        vacancyId: VacancyId,
        scheduledStart: Instant,
    ): Interview? {
        val list =
            repo.findByCandidateIdAndVacancyIdAndScheduledStartOrderByScheduledStartDesc(
                candidateId = candidateId.value,
                vacancyId = vacancyId.value,
                scheduledStart = scheduledStart,
            )
        val entity =
            list.firstOrNull() ?: repo.findFirstByCandidateIdAndVacancyIdOrderByScheduledStartDesc(
                candidateId.value,
                vacancyId.value,
            )
        return entity?.let(mapper::toDomain)
    }
}
