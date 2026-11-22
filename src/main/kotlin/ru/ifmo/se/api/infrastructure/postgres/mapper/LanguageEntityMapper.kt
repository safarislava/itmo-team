package ru.ifmo.se.api.infrastructure.postgres.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.Language
import ru.ifmo.se.api.domain.model.LanguageId
import ru.ifmo.se.api.infrastructure.postgres.entity.LanguageEntity

@Component
class LanguageEntityMapper {
    fun toDomain(entity: LanguageEntity): Language =
        Language(
            id = LanguageId(entity.id),
            name = entity.name,
            version = entity.version,
            sourceFilename = entity.sourceFilename,
            compileCommand = entity.compileCommand,
            runCommand = entity.runCommand,
        )

    fun toEntity(domain: Language): LanguageEntity =
        LanguageEntity(
            id = domain.id.value,
            name = domain.name,
            version = domain.version,
            sourceFilename = domain.sourceFilename,
            compileCommand = domain.compileCommand,
            runCommand = domain.runCommand,
        )
}
