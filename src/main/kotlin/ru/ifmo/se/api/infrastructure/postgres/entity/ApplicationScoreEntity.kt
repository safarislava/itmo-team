package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy

@Entity
@Table(name = "application_score")
class ApplicationScoreEntity(
    @Id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    var application: ApplicationEntity,
    var motivation: Short,
    var softSkills: Short,
    var petProjects: Short,
    var jobExp: Short,
    var hardSkills: Short,
    var workFormat: Short,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false

        val thisEffectiveClass = if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        val otherEffectiveClass = if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        if (thisEffectiveClass != otherEffectiveClass) return false

        other as ApplicationScoreEntity
        return application.id == other.application.id
    }

    override fun hashCode(): Int = application.id.hashCode()
}
