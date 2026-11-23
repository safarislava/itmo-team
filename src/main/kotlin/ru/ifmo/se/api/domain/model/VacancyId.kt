package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class VacancyId(
    val value: UUID,
) {
    override fun toString(): String = value.toString()

    companion object {
        fun generate(): VacancyId = VacancyId(UUID.randomUUID())

        fun from(raw: String): VacancyId = VacancyId(UUID.fromString(raw))

        fun fromOrNull(raw: String): VacancyId? = runCatching { VacancyId(UUID.fromString(raw)) }.getOrNull()
    }
}
