package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.*
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

    var skillIds: List<UUID>?
) : BasicEntity()
