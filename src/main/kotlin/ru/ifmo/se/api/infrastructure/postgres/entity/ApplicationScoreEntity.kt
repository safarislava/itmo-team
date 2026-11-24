package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "application_score")
class ApplicationScoreEntity(
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    var application: ApplicationEntity,
    var motivation: Short,
    var softSkills: Short,
    var petProjects: Short,
    var jobExp: Short,
    var hardSkills: Short,
    var workFormat: Short,
) : BasicEntity() {
    @Id
    override var id: UUID = application.id
}
