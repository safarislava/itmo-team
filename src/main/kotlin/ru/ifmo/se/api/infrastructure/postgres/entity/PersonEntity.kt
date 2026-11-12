package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "person")
class PersonEntity(
    @Id
    @Column(name = "person_id")
    override var id: UUID,

    var name: String,
    var email: String,
    var passwordHash: String,

    @Enumerated(EnumType.STRING)
    var role: PersonRoleEntity,

    var companyId: UUID?,
    var teamId: UUID?,
    var githubId: Long?
) : BasicEntity()