package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import org.springframework.stereotype.Repository
import ru.ifmo.se.api.domain.model.Language
import ru.ifmo.se.api.domain.model.LanguageId
import ru.ifmo.se.api.domain.repository.LanguageRepository
import ru.ifmo.se.api.infrastructure.postgres.mapper.LanguageEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaLanguageRepository

@Repository
class LanguageRepositoryImpl(
    private val repo: JpaLanguageRepository,
    private val mapper: LanguageEntityMapper,
) : LanguageRepository {
    override fun findById(id: LanguageId): Language? = repo.findById(id.value).map(mapper::toDomain).orElse(null)

    override fun findAll(): List<Language> =
        repo
            .findAll(
                org.springframework.data.domain.Sort
                    .by("name", "version"),
            ).map(mapper::toDomain)

    override fun save(language: Language): Language = mapper.toDomain(repo.save(mapper.toEntity(language)))

    override fun delete(id: LanguageId) = repo.deleteById(id.value)
}
