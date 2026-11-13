package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.proxy.HibernateProxy
import ru.ifmo.se.api.domain.model.SubmissionVerdict

@Entity
@Table(name = "submission_response")
class SubmissionResponseEntity(
    @Id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")
    var submission: SubmissionEntity,
    @Enumerated(EnumType.STRING)
    var verdict: SubmissionVerdict?,
    var executionTime: Int?,
    var memoryUsed: Int?,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "runner_server_id")
    var runnerServer: RunnerServerEntity?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false

        val thisEffectiveClass = if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        val otherEffectiveClass = if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        if (thisEffectiveClass != otherEffectiveClass) return false

        other as SubmissionResponseEntity
        return submission.id == other.submission.id
    }

    override fun hashCode(): Int = submission.id.hashCode()
}
