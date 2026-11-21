package ru.ifmo.se.api.infrastructure.postgres.mapper

import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.SkillId
import ru.ifmo.se.api.domain.model.TeamId
import ru.ifmo.se.api.domain.model.Vacancy
import ru.ifmo.se.api.domain.model.VacancyId
import ru.ifmo.se.api.infrastructure.postgres.entity.TeamEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.VacancyEntity

@Component
class VacancyEntityMapper(
    private val entityManager: EntityManager,
) {
    fun toDomain(entity: VacancyEntity): Vacancy =
        Vacancy(
            id = VacancyId(entity.id),
            title = entity.title,
            description = entity.description,
            teamId = TeamId(entity.team.id),
            city = entity.city,
            status = entity.status,
            salary = entity.salary,
            skillIds = entity.skillIds.map { SkillId(it) },
            createdAt = entity.createdAt,
            closedAt = entity.closedAt,
        )

    fun toEntity(domain: Vacancy): VacancyEntity =
        VacancyEntity(
            id = domain.id.value,
            title = domain.title,
            description = domain.description,
            team = entityManager.getReference(TeamEntity::class.java, domain.teamId.value),
            city = domain.city,
            status = domain.status,
            salary = domain.salary,
            skillIds = domain.skillIds.map { it.value },
            createdAt = domain.createdAt,
            closedAt = domain.closedAt,
        )
}
