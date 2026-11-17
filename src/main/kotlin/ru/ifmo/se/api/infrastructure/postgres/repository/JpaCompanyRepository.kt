package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.CompanyEntity
import java.util.UUID

interface JpaCompanyRepository : JpaRepository<CompanyEntity, UUID>
