package ru.ifmo.se.api.presentation.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.Skill
import ru.ifmo.se.api.presentation.dto.response.SkillResponse

@Component
class SkillResponseMapper {
    fun toResponse(domain: Skill): SkillResponse =
        SkillResponse(
            id = domain.id.value,
            name = domain.name,
            description = domain.description,
        )
}
