package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.domain.model.Skill
import ru.ifmo.se.api.domain.model.SkillId
import ru.ifmo.se.api.domain.repository.SkillRepository
import ru.ifmo.se.api.infrastructure.postgres.mapper.SkillEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaFormRepository
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaSkillRepository
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaVacancyRepository

@Repository
class SkillRepositoryImpl(
    private val repo: JpaSkillRepository,
    private val vacancyRepo: JpaVacancyRepository,
    private val formRepo: JpaFormRepository,
    private val mapper: SkillEntityMapper,
) : SkillRepository {
    @Cacheable(value = ["skills"], key = "#id", unless = "#result == null")
    override fun findById(id: SkillId): Skill? = repo.findById(id.value).map(mapper::toDomain).orElse(null)

    override fun findAllById(ids: Iterable<SkillId>): List<Skill> =
        ids.map { it.value }.takeIf { it.isNotEmpty() }?.let { repo.findAllById(it).map(mapper::toDomain) } ?: emptyList()

    @Cacheable(value = ["skills_all"], condition = "#search == null || #search.trim().isEmpty()")
    override fun findAll(search: String?): List<Skill> {
        val trimmedSearch = search?.trim()
        val entities =
            if (trimmedSearch.isNullOrEmpty()) {
                repo.findAllByOrderByNameAsc()
            } else {
                repo.findByNameContainingIgnoreCaseOrderByNameAsc(trimmedSearch)
            }
        return entities.map(mapper::toDomain)
    }

    @Caching(
        evict = [
            CacheEvict(value = ["skills"], key = "#skill.id"),
            CacheEvict(value = ["skills_all"]),
        ],
    )
    override fun save(skill: Skill): Skill = mapper.toDomain(repo.save(mapper.toEntity(skill)))

    @Transactional
    @Caching(
        evict = [
            CacheEvict(value = ["skills"], key = "#id"),
            CacheEvict(value = ["skills_all"]),
        ],
    )
    override fun delete(id: SkillId) {
        vacancyRepo.removeSkillIdFromAll(id.value)
        formRepo.removeSkillIdFromAll(id.value)
        repo.deleteById(id.value)
    }
}
