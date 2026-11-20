package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Repository
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.SortDirection
import ru.ifmo.se.api.domain.model.Task
import ru.ifmo.se.api.domain.model.TaskFilter
import ru.ifmo.se.api.domain.model.TaskId
import ru.ifmo.se.api.domain.repository.TaskRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.PersonEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.TaskEntity
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

        val direction = if (pageQuery.sortOrder == SortDirection.DESC) Sort.Direction.DESC else Sort.Direction.ASC
        val sortProperty =
            when (pageQuery.sortBy) {
                "name" -> "name"
                "timeLimit" -> "timeLimit"
                "memoryLimit" -> "memoryLimit"
                else -> "id"
            }

        val pageable = PageRequest.of(pageQuery.pageNumber, pageQuery.pageSize, Sort.by(direction, sortProperty))
        val pagedResult = repo.findAll(specification, pageable)

        return PageResult(
            items = pagedResult.content.map(mapper::toDomain),
            totalElements = pagedResult.totalElements,
            pageNumber = pagedResult.number,
            totalPages = pagedResult.totalPages,
        )
    }

    override fun save(task: Task): Task = mapper.toDomain(repo.save(mapper.toEntity(task)))

    override fun delete(id: TaskId) {
        repo.deleteById(id.value)
    }
}
