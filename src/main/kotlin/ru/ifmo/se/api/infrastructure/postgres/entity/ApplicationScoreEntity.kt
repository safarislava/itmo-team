package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "application_score")
class ApplicationScoreEntity(
    @Id
    @Column(name = "application_id")
    override var id: UUID,

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "application_id")
    var application: ApplicationEntity,

    var motivation: Short,
    var softSkills: Short,
    var petProjects: Short,
    var jobExp: Short,
    var hardSkills: Short,
    var workFormat: Short
) : BasicEntity()
