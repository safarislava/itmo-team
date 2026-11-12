package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "language")
class LanguageEntity(
    @Id
    @Column(name = "language_id")
    override var id: UUID,
    var name: String,
    var version: String,
) : BasicEntity()
