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
import ru.ifmo.se.api.domain.model.SubmissionVerdict
import java.util.UUID

@Entity
@Table(name = "submission_response")
class SubmissionResponseEntity(
    @Id
    @Column(name = "submission_id")
    override var id: UUID,
    @Enumerated(EnumType.STRING)
    var verdict: SubmissionVerdict?,
    var executionTime: Int?,
    var memoryUsed: Int?,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "runner_server_id")
    var runnerServer: RunnerServerEntity?,
) : BasicEntity()
