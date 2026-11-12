package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "coder_report")
class CoderReportEntity(
    @Id
    @Column(name = "report_id")
    override var id: UUID,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coder_id")
    var coder: PersonEntity?,
    var countOpenedPr: Int,
    var countClosedPr: Int,
    var countReviewedPr: Int,
    var countCommits: Int,
    var reportDate: Instant,
) : BasicEntity()
