package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import jakarta.persistence.criteria.JoinType
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.domain.model.ContestId
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.Submission
import ru.ifmo.se.api.domain.model.SubmissionFilter
import ru.ifmo.se.api.domain.model.SubmissionId
import ru.ifmo.se.api.domain.model.SubmissionStatus
import ru.ifmo.se.api.domain.model.TaskId
import ru.ifmo.se.api.domain.repository.SubmissionRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.CompanyEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.ContestEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.PersonEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.SubmissionEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.TaskEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.TeamEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.VacancyEntity
import ru.ifmo.se.api.infrastructure.postgres.extension.toPageRequest
import ru.ifmo.se.api.infrastructure.postgres.extension.toPageResult
import ru.ifmo.se.api.infrastructure.postgres.mapper.SubmissionEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaSubmissionRepository
import java.util.UUID

@Repository
class SubmissionRepositoryImpl(
    private val repo: JpaSubmissionRepository,
    private val mapper: SubmissionEntityMapper,
) : SubmissionRepository {
    override fun findById(id: SubmissionId): Submission? = repo.findById(id.value).map(mapper::toDomain).orElse(null)

    override fun findAll(
        filter: SubmissionFilter,
        pageQuery: PageQuery,
    ): PageResult<Submission> {
        val specification =
            Specification<SubmissionEntity> { root, query, cb ->
                query.distinct(true)
                val predicates =
                    buildList {
                        filter.contestId?.let {
                            add(cb.equal(root.get<ContestEntity>("contest").get<UUID>("id"), it.value))
                        }
                        filter.taskId?.let {
                            add(cb.equal(root.get<TaskEntity>("task").get<UUID>("id"), it.value))
                        }
                        filter.senderId?.let {
                            add(cb.equal(root.get<PersonEntity>("sender").get<UUID>("id"), it.value))
                        }
                        filter.status?.let {
                            add(cb.equal(root.get<SubmissionStatus>("status"), it))
                        }
                        filter.companyId?.let { compId ->
                            val contestJoin = root.join<SubmissionEntity, ContestEntity>("contest", JoinType.INNER)
                            val vacancies = contestJoin.join<ContestEntity, VacancyEntity>("vacancies", JoinType.INNER)
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

        val pageable = pageQuery.toPageRequest { it.takeIf { it.isNotBlank() } ?: "submittedAt" }
        return repo.findAll(specification, pageable).toPageResult(mapper::toDomain)
    }

    override fun save(submission: Submission): Submission = mapper.toDomain(repo.save(mapper.toEntity(submission)))

    @Transactional
    override fun updateAiScore(
        id: SubmissionId,
        aiScore: Short,
    ) = repo.updateAiScore(id.value, aiScore)

    override fun countAttempts(
        senderId: PersonId,
        contestId: ContestId,
        taskId: TaskId,
    ): Int = repo.countBySenderIdAndContestIdAndTaskId(senderId.value, contestId.value, taskId.value).toInt()
}
