package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import ru.ifmo.se.api.infrastructure.postgres.entity.PersonEntity
import java.util.Optional
import java.util.UUID

interface JpaPersonRepository :
    JpaRepository<PersonEntity, UUID>,
    JpaSpecificationExecutor<PersonEntity> {
    fun findByEmail(email: String): Optional<PersonEntity>

    fun existsByEmail(email: String): Boolean

    fun findByGithubId(githubId: Long): Optional<PersonEntity>

    fun findAllByTeamId(teamId: UUID): List<PersonEntity>

    fun findAllByCompanyId(companyId: UUID): List<PersonEntity>
}
