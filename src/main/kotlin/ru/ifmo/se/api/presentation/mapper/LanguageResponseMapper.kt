package ru.ifmo.se.api.presentation.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.Language
import ru.ifmo.se.api.presentation.dto.response.LanguageResponse

@Component
class LanguageResponseMapper {
    fun toResponse(domain: Language): LanguageResponse =
        LanguageResponse(
            id = domain.id.value,
            name = domain.name,
            version = domain.version,
        )
}
