package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "skill")
class SkillEntity(
    @Id
    @Column(name = "skill_id")
    override var id: UUID,
    var name: String,
    var description: String?,
) : BasicEntity()
