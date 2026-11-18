package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.VacancyId
import ru.ifmo.se.api.infrastructure.postgres.entity.VacancyEntity

interface VacancyRepository {
    fun findEntityById(id: VacancyId): VacancyEntity?
}
