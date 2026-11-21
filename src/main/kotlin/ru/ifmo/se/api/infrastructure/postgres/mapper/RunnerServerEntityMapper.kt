package ru.ifmo.se.api.infrastructure.postgres.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.RunnerServer
import ru.ifmo.se.api.domain.model.RunnerServerId
import ru.ifmo.se.api.infrastructure.postgres.entity.RunnerServerEntity

@Component
class RunnerServerEntityMapper {
    fun toDomain(entity: RunnerServerEntity): RunnerServer =
        RunnerServer(
            id = RunnerServerId(entity.id),
            ip = entity.ip,
        )

    fun toEntity(domain: RunnerServer): RunnerServerEntity =
        RunnerServerEntity(
            id = domain.id.value,
            ip = domain.ip,
        )
}
