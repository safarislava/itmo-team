package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

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
    override fun findById(id: SkillId): Skill? = repo.findById(id.value).map(mapper::toDomain).orElse(null)

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

    override fun save(skill: Skill): Skill = mapper.toDomain(repo.save(mapper.toEntity(skill)))

    @Transactional
    override fun delete(id: SkillId) {
        vacancyRepo.removeSkillIdFromAll(id.value)
        formRepo.removeSkillIdFromAll(id.value)
        repo.deleteById(id.value)
    }
}
