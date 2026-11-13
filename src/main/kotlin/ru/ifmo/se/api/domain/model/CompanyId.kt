package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class CompanyId(
    val value: UUID,
) {
    override fun toString(): String = value.toString()

    companion object {
        fun generate(): CompanyId = CompanyId(UUID.randomUUID())

        fun from(raw: String): CompanyId = CompanyId(UUID.fromString(raw))
    }
}
