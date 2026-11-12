package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.*
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
