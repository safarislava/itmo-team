package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import ru.ifmo.se.api.domain.model.InterviewDecision
import java.util.UUID

@Entity
@Table(name = "interview_feedback")
class InterviewFeedbackEntity(
    @Id
    @Column(name = "feedback_id")
    override var id: UUID,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id")
    var interview: InterviewEntity,
    var review: String,
    var score: Short,
    @Enumerated(EnumType.STRING)
    var decision: InterviewDecision,
) : BasicEntity()
