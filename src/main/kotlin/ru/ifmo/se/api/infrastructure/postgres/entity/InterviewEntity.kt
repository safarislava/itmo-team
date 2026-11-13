package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import ru.ifmo.se.api.domain.model.InterviewStatus
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "interview")
class InterviewEntity(
    @Id
    @Column(name = "interview_id")
    override var id: UUID,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id")
    var candidate: PersonEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teamlead_id")
    var teamlead: PersonEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vacancy_id")
    var vacancy: VacancyEntity,
    @Column(name = "video_s3key")
    var videoS3key: String?,
    var scheduledStart: Instant,
    var scheduledEnd: Instant,
    @Enumerated(EnumType.STRING)
    var status: InterviewStatus,
) : BasicEntity()
