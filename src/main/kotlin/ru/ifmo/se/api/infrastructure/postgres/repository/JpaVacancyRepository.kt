package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.VacancyEntity
import java.util.UUID

interface JpaVacancyRepository : JpaRepository<VacancyEntity, UUID>
