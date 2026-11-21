package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.SkillEntity
import java.util.UUID

interface JpaSkillRepository : JpaRepository<SkillEntity, UUID> {
    fun findAllByOrderByNameAsc(): List<SkillEntity>

    fun findByNameContainingIgnoreCaseOrderByNameAsc(search: String): List<SkillEntity>
}
