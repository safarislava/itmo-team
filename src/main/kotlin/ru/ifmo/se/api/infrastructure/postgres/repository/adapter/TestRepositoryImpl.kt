package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.domain.model.TaskId
import ru.ifmo.se.api.domain.model.Test
import ru.ifmo.se.api.domain.model.TestId
import ru.ifmo.se.api.domain.repository.TestRepository
import ru.ifmo.se.api.infrastructure.postgres.mapper.TestEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaTestRepository

@Repository
class TestRepositoryImpl(
    private val repo: JpaTestRepository,
    private val mapper: TestEntityMapper,
) : TestRepository {
    override fun findById(id: TestId): Test? = repo.findById(id.value).map(mapper::toDomain).orElse(null)

    override fun findByTaskId(taskId: TaskId): List<Test> = repo.findByTaskId(taskId.value).map(mapper::toDomain)

    override fun findVisibleByTaskId(taskId: TaskId): List<Test> = repo.findByTaskIdAndIsHiddenFalse(taskId.value).map(mapper::toDomain)

    override fun save(test: Test): Test = mapper.toDomain(repo.save(mapper.toEntity(test)))

    @Transactional
    override fun saveAll(tests: List<Test>): List<Test> = repo.saveAll(tests.map(mapper::toEntity)).map(mapper::toDomain)

    override fun delete(id: TestId) {
        repo.deleteById(id.value)
    }

    @Transactional
    override fun deleteAllByTaskId(taskId: TaskId) {
        repo.deleteByTaskId(taskId.value)
    }
}
