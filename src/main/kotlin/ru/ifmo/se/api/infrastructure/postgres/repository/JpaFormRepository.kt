package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.FormEntity
import java.util.UUID

interface JpaFormRepository : JpaRepository<FormEntity, UUID>
