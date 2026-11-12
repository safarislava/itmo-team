package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "application_response")
class ApplicationResponseEntity(
    @Id
    @Column(name = "response_id")
    override var id: UUID,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    var application: ApplicationEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hr_id")
    var hr: PersonEntity,
    var motivation: Short,
    var softSkills: Short,
    var petProjects: Short,
    var jobExp: Short,
    var hardSkills: Short,
    var workFormat: Short,
) : BasicEntity()
