package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.ResumeEntity
import java.util.UUID

interface JpaResumeRepository : JpaRepository<ResumeEntity, UUID>
