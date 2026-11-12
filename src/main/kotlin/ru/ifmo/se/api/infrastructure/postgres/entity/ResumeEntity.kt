package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "resume")
class ResumeEntity(
    @Id
    @Column(name = "resume_id")
    override var id: UUID,

    var s3key: String,
    var filename: String
) : BasicEntity()
