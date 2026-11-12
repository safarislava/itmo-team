package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "submission")
class SubmissionEntity(
    @Id
    @Column(name = "submission_id")
    override var id: UUID,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    var sender: PersonEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id")
    var contest: ContestEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    var task: TaskEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    var language: LanguageEntity,
    @Column(name = "snapshot_s3key")
    var snapshotS3key: String,
    @Enumerated(EnumType.STRING)
    var status: SubmissionStatus,
    var attempt: Short,
    var aiScore: Short?,
    var submittedAt: Instant,
) : BasicEntity()
