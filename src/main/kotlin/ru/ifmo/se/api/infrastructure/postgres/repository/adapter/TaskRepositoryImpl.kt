package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Repository
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.Task
import ru.ifmo.se.api.domain.model.TaskFilter
import ru.ifmo.se.api.domain.model.TaskId
import ru.ifmo.se.api.domain.repository.TaskRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.PersonEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.TaskEntity
import ru.ifmo.se.api.infrastructure.postgres.extension.toPageRequest
import ru.ifmo.se.api.infrastructure.postgres.extension.toPageResult
import ru.ifmo.se.api.infrastructure.postgres.mapper.TaskEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaTaskRepository
import java.util.UUID

@Repository
class TaskRepositoryImpl(
    private val repo: JpaTaskRepository,
    private val mapper: TaskEntityMapper,
) : TaskRepository {
    override fun findById(id: TaskId): Task? = repo.findById(id.value).map(mapper::toDomain).orElse(null)

    override fun findAll(
        filter: TaskFilter,
        pageQuery: PageQuery,
    ): PageResult<Task> {
        val specification =
            Specification<TaskEntity> { root, _, cb ->
                val predicates = mutableListOf<Predicate>()

                filter.search?.takeIf { it.isNotBlank() }?.let { query ->
                    val pattern = "%${query.trim().lowercase()}%"
                    predicates.add(cb.like(cb.lower(root.get("name")), pattern))
                }

                filter.authorId?.let {
                    predicates.add(cb.equal(root.get<PersonEntity>("author").get<UUID>("id"), it.value))
                }

                cb.and(*predicates.toTypedArray())
            }

        val pageable =
            pageQuery.toPageRequest {
                when (it) {
                    "name" -> "name"
                    "timeLimit" -> "timeLimit"
                    "memoryLimit" -> "memoryLimit"
                    else -> "id"
                }
            }
        return repo.findAll(specification, pageable).toPageResult(mapper::toDomain)
    }

    override fun save(task: Task): Task = mapper.toDomain(repo.save(mapper.toEntity(task)))

    override fun delete(id: TaskId) {
        repo.deleteById(id.value)
    }
}
