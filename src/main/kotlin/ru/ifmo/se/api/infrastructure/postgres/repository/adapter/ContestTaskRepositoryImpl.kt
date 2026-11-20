package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.domain.model.ContestId
import ru.ifmo.se.api.domain.model.ContestTask
import ru.ifmo.se.api.domain.model.Task
import ru.ifmo.se.api.domain.model.TaskId
import ru.ifmo.se.api.domain.repository.ContestTaskRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.ContestTaskId
import ru.ifmo.se.api.infrastructure.postgres.mapper.ContestTaskEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.mapper.TaskEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaContestTaskRepository

@Repository
class ContestTaskRepositoryImpl(
    private val contestTaskRepo: JpaContestTaskRepository,
    private val contestTaskMapper: ContestTaskEntityMapper,
    private val taskMapper: TaskEntityMapper,
) : ContestTaskRepository {
    override fun findById(
        contestId: ContestId,
        taskId: TaskId,
    ): ContestTask? =
        contestTaskRepo
            .findById(ContestTaskId(contestId.value, taskId.value))
            .map(contestTaskMapper::toDomain)
            .orElse(null)

    override fun findByContestId(contestId: ContestId): List<ContestTask> =
        contestTaskRepo.findByIdContestId(contestId.value).map(contestTaskMapper::toDomain)

    @Transactional(readOnly = true)
    override fun findTasksByContestId(contestId: ContestId): List<Task> =
        contestTaskRepo.findByIdContestId(contestId.value).map { taskMapper.toDomain(it.task) }

    @Transactional
    override fun save(contestTask: ContestTask): ContestTask =
        contestTaskMapper.toDomain(contestTaskRepo.save(contestTaskMapper.toEntity(contestTask)))

    @Transactional
    override fun delete(
        contestId: ContestId,
        taskId: TaskId,
    ) {
        contestTaskRepo.deleteByIdContestIdAndIdTaskId(contestId.value, taskId.value)
    }
}
