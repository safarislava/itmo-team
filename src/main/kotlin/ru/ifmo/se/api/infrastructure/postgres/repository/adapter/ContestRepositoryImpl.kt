package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import jakarta.persistence.criteria.JoinType
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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
import ru.ifmo.se.api.domain.model.SortDirection
import ru.ifmo.se.api.domain.model.Task
import ru.ifmo.se.api.domain.model.TaskId
import ru.ifmo.se.api.domain.repository.ContestRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.CompanyEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.ContestEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.PersonEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.TeamEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.VacancyEntity
import ru.ifmo.se.api.infrastructure.postgres.mapper.ContestEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.mapper.TaskEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaContestRepository
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaTaskRepository
import java.time.Instant
import java.util.UUID

@Repository
class ContestRepositoryImpl(
    private val repo: JpaContestRepository,
    private val mapper: ContestEntityMapper,
    private val taskRepo: JpaTaskRepository,
    private val taskMapper: TaskEntityMapper,
) : ContestRepository {
    override fun findById(id: ContestId): Contest? = repo.findById(id.value).map(mapper::toDomain).orElse(null)

    override fun findCompanyIdsByContestId(contestId: ContestId): Set<CompanyId> =
        repo.findCompanyIdsByContestId(contestId.value).map(::CompanyId).toSet()

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

        val direction = if (pageQuery.sortOrder == SortDirection.DESC) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(pageQuery.pageNumber, pageQuery.pageSize, Sort.by(direction, "scheduledTime"))
        val pagedResult = repo.findAll(specification, pageable)

        return PageResult(
            items = pagedResult.content.map(mapper::toDomain),
            totalElements = pagedResult.totalElements,
            pageNumber = pagedResult.number,
            totalPages = pagedResult.totalPages,
        )
    }

    override fun save(contest: Contest): Contest = mapper.toDomain(repo.save(mapper.toEntity(contest)))

    override fun delete(id: ContestId) = repo.deleteById(id.value)

    @Transactional
    override fun addTask(
        contestId: ContestId,
        taskId: TaskId,
    ) {
        val contest = repo.findById(contestId.value).orElse(null) ?: return
        if (contest.tasks.none { it.id == taskId.value }) {
            contest.tasks.add(taskRepo.getReferenceById(taskId.value))
            repo.save(contest)
        }
    }

    @Transactional
    override fun removeTask(
        contestId: ContestId,
        taskId: TaskId,
    ) {
        repo.findById(contestId.value).ifPresent { contest ->
            contest.tasks.removeIf { it.id == taskId.value }
            repo.save(contest)
        }
    }

    @Transactional(readOnly = true)
    override fun findTasksByContestId(contestId: ContestId): List<Task> =
        repo.findById(contestId.value).map { it.tasks.map(taskMapper::toDomain) }.orElse(emptyList())

    override fun isCandidateParticipantOfRunningContestWithTask(
        candidateId: PersonId,
        taskId: TaskId,
        now: Instant,
    ): Boolean {
        val contests = repo.findByParticipantsIdAndTasksId(candidateId.value, taskId.value)
        return contests.any { it.scheduledTime <= now && now <= it.scheduledTime.plus(it.duration) }
    }

    override fun isParticipant(
        contestId: ContestId,
        candidateId: PersonId,
    ): Boolean = repo.existsByIdAndParticipantsId(contestId.value, candidateId.value)
}
