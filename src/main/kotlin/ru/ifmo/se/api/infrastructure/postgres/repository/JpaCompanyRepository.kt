package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import ru.ifmo.se.api.infrastructure.postgres.entity.CompanyEntity
import java.util.UUID

interface JpaCompanyRepository :
    JpaRepository<CompanyEntity, UUID>,
    JpaSpecificationExecutor<CompanyEntity>
