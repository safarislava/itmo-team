package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class VacancyId(
    val value: UUID,
) {
    companion object {
        fun generate(): VacancyId = VacancyId(UUID.randomUUID())
    }
}
