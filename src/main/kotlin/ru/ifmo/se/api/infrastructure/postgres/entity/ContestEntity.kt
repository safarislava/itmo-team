package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "contest")
class ContestEntity(
    @Id
    @Column(name = "contest_id")
    override var id: UUID,
    var scheduledTime: Instant,
    var duration: Duration,
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "contest_task",
        joinColumns = [JoinColumn(name = "contest_id")],
        inverseJoinColumns = [JoinColumn(name = "task_id")],
    )
    var tasks: MutableList<TaskEntity> = mutableListOf(),
    @ManyToMany(mappedBy = "contests", fetch = FetchType.LAZY)
    var vacancies: MutableList<VacancyEntity> = mutableListOf(),
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "contest_participant",
        joinColumns = [JoinColumn(name = "contest_id")],
        inverseJoinColumns = [JoinColumn(name = "person_id")],
    )
    var participants: MutableList<PersonEntity> = mutableListOf(),
) : BasicEntity()
