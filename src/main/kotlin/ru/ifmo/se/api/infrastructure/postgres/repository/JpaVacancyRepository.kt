package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.ifmo.se.api.infrastructure.postgres.entity.VacancyEntity
import java.util.UUID

interface JpaVacancyRepository : JpaRepository<VacancyEntity, UUID> {
    @Modifying
    @Query(
        value = "UPDATE vacancy SET skill_ids = array_remove(skill_ids, :skillId) WHERE :skillId = ANY(skill_ids)",
        nativeQuery = true,
    )
    fun removeSkillIdFromAll(
        @Param("skillId") skillId: UUID,
    )
}
