package ru.ifmo.se.api.infrastructure.postgres.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.Skill
import ru.ifmo.se.api.domain.model.SkillId
import ru.ifmo.se.api.infrastructure.postgres.entity.SkillEntity

@Component
class SkillEntityMapper {
    fun toDomain(entity: SkillEntity): Skill =
        Skill(
            id = SkillId(entity.id),
            name = entity.name,
            description = entity.description,
        )

    fun toEntity(domain: Skill): SkillEntity =
        SkillEntity(
            id = domain.id.value,
            name = domain.name,
            description = domain.description,
        )
}
