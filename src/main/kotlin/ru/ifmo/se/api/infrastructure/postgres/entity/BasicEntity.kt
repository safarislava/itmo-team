package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.MappedSuperclass
import org.hibernate.proxy.HibernateProxy
import java.util.UUID

@MappedSuperclass
abstract class BasicEntity {
    abstract var id: UUID

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false

        val thisEffectiveClass = if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        val otherEffectiveClass = if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        if (thisEffectiveClass != otherEffectiveClass) return false

        other as BasicEntity
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
