package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "company")
class CompanyEntity(
    @Id
    @Column(name = "company_id")
    override var id: UUID,

    var name: String,
    var creationDate: LocalDate,
) : BasicEntity()