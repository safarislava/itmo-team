package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import ru.ifmo.se.api.domain.model.VacancyStatus
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "vacancy")
class VacancyEntity(
    @Id
    @Column(name = "vacancy_id")
    override var id: UUID,
    var title: String,
    var description: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    var team: TeamEntity,
    var city: String?,
    @Enumerated(EnumType.STRING)
    var status: VacancyStatus,
    var salary: Int?,
    var skillIds: List<UUID>,
    var createdAt: Instant,
    var closedAt: Instant?,
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "vacancy_contest",
        joinColumns = [JoinColumn(name = "vacancy_id")],
        inverseJoinColumns = [JoinColumn(name = "contest_id")],
    )
    var contests: MutableList<ContestEntity> = mutableListOf(),
) : BasicEntity()
