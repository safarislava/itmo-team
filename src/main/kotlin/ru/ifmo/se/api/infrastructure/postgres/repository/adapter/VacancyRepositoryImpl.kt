package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import org.springframework.stereotype.Repository
import ru.ifmo.se.api.domain.model.VacancyId
import ru.ifmo.se.api.domain.repository.VacancyRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.VacancyEntity
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaVacancyRepository

@Repository
class VacancyRepositoryImpl(
    private val repo: JpaVacancyRepository,
) : VacancyRepository {
    override fun findEntityById(id: VacancyId): VacancyEntity? = repo.findById(id.value).orElse(null)
}
