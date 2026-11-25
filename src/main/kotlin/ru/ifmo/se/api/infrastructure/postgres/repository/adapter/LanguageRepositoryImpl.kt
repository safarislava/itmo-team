package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
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
    @Cacheable(value = ["languages"], key = "#id", unless = "#result == null")
    override fun findById(id: LanguageId): Language? = repo.findById(id.value).map(mapper::toDomain).orElse(null)

    override fun findAllById(ids: Iterable<LanguageId>): List<Language> =
        ids.map { it.value }.takeIf { it.isNotEmpty() }?.let { repo.findAllById(it).map(mapper::toDomain) } ?: emptyList()

    @Cacheable(value = ["languages_all"])
    override fun findAll(): List<Language> =
        repo
            .findAll(
                org.springframework.data.domain.Sort
                    .by("name", "version"),
            ).map(mapper::toDomain)

    @Caching(
        evict = [
            CacheEvict(value = ["languages"], key = "#language.id"),
            CacheEvict(value = ["languages_all"]),
        ],
    )
    override fun save(language: Language): Language = mapper.toDomain(repo.save(mapper.toEntity(language)))

    @Caching(
        evict = [
            CacheEvict(value = ["languages"], key = "#id"),
            CacheEvict(value = ["languages_all"]),
        ],
    )
    override fun delete(id: LanguageId) = repo.deleteById(id.value)
}
