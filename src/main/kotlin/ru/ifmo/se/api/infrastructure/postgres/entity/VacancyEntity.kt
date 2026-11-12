package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.*
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
