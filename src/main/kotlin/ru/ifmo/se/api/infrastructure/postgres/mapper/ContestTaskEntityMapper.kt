package ru.ifmo.se.api.infrastructure.postgres.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.ContestId
import ru.ifmo.se.api.domain.model.ContestTask
import ru.ifmo.se.api.domain.model.TaskId
import ru.ifmo.se.api.infrastructure.postgres.entity.ContestTaskEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.ContestTaskId
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaContestRepository
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaTaskRepository

@Component
class ContestTaskEntityMapper(
    private val contestRepository: JpaContestRepository,
    private val taskRepository: JpaTaskRepository,
) {
    fun toDomain(entity: ContestTaskEntity): ContestTask =
        ContestTask(
            contestId = ContestId(entity.id.contestId),
            taskId = TaskId(entity.id.taskId),
            maxAttempts = entity.maxAttempts.toInt(),
        )

    fun toEntity(domain: ContestTask): ContestTaskEntity =
        ContestTaskEntity(
            id = ContestTaskId(contestId = domain.contestId.value, taskId = domain.taskId.value),
            contest = contestRepository.getReferenceById(domain.contestId.value),
            task = taskRepository.getReferenceById(domain.taskId.value),
            maxAttempts = domain.maxAttempts.toShort(),
        )
}
