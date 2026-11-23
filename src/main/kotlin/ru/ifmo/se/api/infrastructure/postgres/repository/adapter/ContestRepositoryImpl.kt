package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import jakarta.persistence.criteria.JoinType
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.Contest
import ru.ifmo.se.api.domain.model.ContestFilter
import ru.ifmo.se.api.domain.model.ContestId
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.TaskId
import ru.ifmo.se.api.domain.model.VacancyId
import ru.ifmo.se.api.domain.repository.ContestRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.CompanyEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.ContestEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.PersonEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.TeamEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.VacancyEntity
import ru.ifmo.se.api.infrastructure.postgres.extension.toPageRequest
import ru.ifmo.se.api.infrastructure.postgres.extension.toPageResult
import ru.ifmo.se.api.infrastructure.postgres.mapper.ContestEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaContestRepository
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaVacancyRepository
import java.time.Instant
import java.util.UUID

@Repository
class ContestRepositoryImpl(
    private val repo: JpaContestRepository,
    private val vacancyRepo: JpaVacancyRepository,
    private val mapper: ContestEntityMapper,
) : ContestRepository {
    override fun findById(id: ContestId): Contest? = repo.findById(id.value).map(mapper::toDomain).orElse(null)

    override fun findCompanyIdsByContestId(contestId: ContestId): Set<CompanyId> =
        vacancyRepo
            .findAllByContestsId(contestId.value)
            .map { CompanyId(it.team.company.id) }
            .toSet()

    override fun findAll(
        filter: ContestFilter,
        pageQuery: PageQuery,
    ): PageResult<Contest> {
        val specification =
            Specification<ContestEntity> { root, query, cb ->
                query.distinct(true)
                val predicates =
                    buildList {
                        filter.dateFrom?.let { add(cb.greaterThanOrEqualTo(root.get("scheduledTime"), it)) }
                        filter.dateTo?.let { add(cb.lessThanOrEqualTo(root.get("scheduledTime"), it)) }
                        filter.vacancyId?.let { vacId ->
                            val vacancies = root.join<ContestEntity, VacancyEntity>("vacancies", JoinType.INNER)
                            add(cb.equal(vacancies.get<UUID>("id"), vacId.value))
                        }
                        filter.participantId?.let { partId ->
                            val participants = root.join<ContestEntity, PersonEntity>("participants", JoinType.INNER)
                            add(cb.equal(participants.get<UUID>("id"), partId.value))
                        }
                        filter.companyId?.let { compId ->
                            val vacancies = root.join<ContestEntity, VacancyEntity>("vacancies", JoinType.INNER)
                            add(
                                cb.equal(
                                    vacancies
                                        .get<TeamEntity>("team")
                                        .get<CompanyEntity>("company")
                                        .get<UUID>("id"),
                                    compId.value,
                                ),
                            )
                        }
                    }
                cb.and(*predicates.toTypedArray())
            }

        val pageable = pageQuery.toPageRequest("scheduledTime")
        return repo.findAll(specification, pageable).toPageResult(mapper::toDomain)
    }

    override fun save(contest: Contest): Contest = mapper.toDomain(repo.save(mapper.toEntity(contest)))

    override fun delete(id: ContestId) = repo.deleteById(id.value)

    override fun isCandidateParticipantOfRunningContestWithTask(
        candidateId: PersonId,
        taskId: TaskId,
        now: Instant,
    ): Boolean {
        val contests = repo.findByParticipantsIdAndTasksId(candidateId.value, taskId.value)
        return contests.any { it.scheduledTime <= now && now <= it.scheduledTime.plus(it.duration) }
    }

    @Transactional(readOnly = true)
    override fun isParticipant(
        contestId: ContestId,
        candidateId: PersonId,
    ): Boolean =
        repo
            .findById(contestId.value)
            .map { contest -> contest.participants.any { it.id == candidateId.value } }
            .orElse(false)

    override fun findByVacancyId(vacancyId: VacancyId): List<Contest> =
        repo.findAllByVacanciesIdOrderByScheduledTimeAsc(vacancyId.value).map(mapper::toDomain)

    @Transactional
    override fun addParticipant(
        contestId: ContestId,
        personId: PersonId,
    ) {
        repo.addParticipant(contestId.value, personId.value)
    }
}
