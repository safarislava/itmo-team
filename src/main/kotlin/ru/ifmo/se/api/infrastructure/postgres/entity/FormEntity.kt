package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "form")
class FormEntity(
    @Id
    @Column(name = "form_id")
    override var id: UUID,
    var university: String?,
    var motivation: String?,
    var petProjects: String?,
    var jobExp: String?,
    @Enumerated(EnumType.STRING)
    var workFormat: WorkFormat?,
    var skillIds: List<UUID>?,
) : BasicEntity()
