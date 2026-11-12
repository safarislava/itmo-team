package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.*
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
    var status: InterviewStatus
) : BasicEntity()
