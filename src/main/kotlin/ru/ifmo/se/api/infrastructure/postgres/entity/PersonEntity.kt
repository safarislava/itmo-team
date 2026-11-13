package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import ru.ifmo.se.api.domain.model.PersonRole
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
    var role: PersonRole,
    var companyId: UUID?,
    var teamId: UUID?,
    var githubId: Long?,
) : BasicEntity()
