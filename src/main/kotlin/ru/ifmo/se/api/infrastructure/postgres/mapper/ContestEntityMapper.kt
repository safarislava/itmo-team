package ru.ifmo.se.api.infrastructure.postgres.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.Contest
import ru.ifmo.se.api.domain.model.ContestId
import ru.ifmo.se.api.infrastructure.postgres.entity.ContestEntity

@Component
class ContestEntityMapper {
    fun toDomain(entity: ContestEntity): Contest =
        Contest(
            id = ContestId(entity.id),
            name = entity.name,
            scheduledTime = entity.scheduledTime,
            duration = entity.duration,
        )

    fun toEntity(domain: Contest): ContestEntity =
        ContestEntity(
            id = domain.id.value,
            name = domain.name,
            scheduledTime = domain.scheduledTime,
            duration = domain.duration,
        )
}
