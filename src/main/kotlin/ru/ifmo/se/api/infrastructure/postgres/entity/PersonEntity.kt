package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "person")
class PersonEntity(
    @Id
    @Column(name = "person_id", nullable = false, updatable = false)
    override var id: UUID,

    var name: String,
    var email: String,
    var passwordHash: String,

    @Enumerated(EnumType.STRING)
    var role: PersonRoleEntity,

    var companyId: UUID? = null,
    var teamId: UUID? = null,
    var githubId: Long? = null
) : BasicEntity()