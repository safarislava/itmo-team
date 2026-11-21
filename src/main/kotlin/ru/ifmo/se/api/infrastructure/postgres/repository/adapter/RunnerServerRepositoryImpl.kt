package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import org.springframework.stereotype.Repository
import ru.ifmo.se.api.domain.model.RunnerServer
import ru.ifmo.se.api.domain.model.RunnerServerId
import ru.ifmo.se.api.domain.repository.RunnerServerRepository
import ru.ifmo.se.api.infrastructure.postgres.mapper.RunnerServerEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaRunnerServerRepository

@Repository
class RunnerServerRepositoryImpl(
    private val repo: JpaRunnerServerRepository,
    private val mapper: RunnerServerEntityMapper,
) : RunnerServerRepository {
    override fun findById(id: RunnerServerId): RunnerServer? = repo.findById(id.value).map(mapper::toDomain).orElse(null)

    override fun findAll(): List<RunnerServer> = repo.findAll().map(mapper::toDomain)
}
