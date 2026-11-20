package ru.ifmo.se.api.infrastructure.postgres.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.Task
import ru.ifmo.se.api.domain.model.TaskId
import ru.ifmo.se.api.infrastructure.postgres.entity.TaskEntity
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaPersonRepository

@Component
class TaskEntityMapper(
    private val personRepository: JpaPersonRepository,
) {
    fun toDomain(entity: TaskEntity): Task =
        Task(
            id = TaskId(entity.id),
            authorId = PersonId(entity.author.id),
            name = entity.name,
            description = entity.description,
            timeLimit = entity.timeLimit,
            memoryLimit = entity.memoryLimit,
        )

    fun toEntity(domain: Task): TaskEntity =
        TaskEntity(
            id = domain.id.value,
            author = personRepository.getReferenceById(domain.authorId.value),
            name = domain.name,
            description = domain.description,
            timeLimit = domain.timeLimit,
            memoryLimit = domain.memoryLimit,
        )
}
