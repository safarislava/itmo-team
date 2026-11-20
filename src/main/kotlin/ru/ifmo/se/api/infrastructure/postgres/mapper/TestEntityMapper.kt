package ru.ifmo.se.api.infrastructure.postgres.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.TaskId
import ru.ifmo.se.api.domain.model.Test
import ru.ifmo.se.api.domain.model.TestId
import ru.ifmo.se.api.infrastructure.postgres.entity.TestEntity
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaTaskRepository

@Component
class TestEntityMapper(
    private val taskRepository: JpaTaskRepository,
) {
    fun toDomain(entity: TestEntity): Test =
        Test(
            id = TestId(entity.id),
            taskId = TaskId(entity.task.id),
            inputS3key = entity.inputS3key,
            outputS3key = entity.outputS3key,
            isHidden = entity.isHidden,
        )

    fun toEntity(domain: Test): TestEntity =
        TestEntity(
            id = domain.id.value,
            task = taskRepository.getReferenceById(domain.taskId.value),
            inputS3key = domain.inputS3key,
            outputS3key = domain.outputS3key,
            isHidden = domain.isHidden,
        )
}
