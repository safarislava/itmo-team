package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class CoderReportId(
    val value: UUID,
) {
    override fun toString(): String = value.toString()

    companion object {
        fun generate(): CoderReportId = CoderReportId(uuidV7())

        fun from(raw: String): CoderReportId = CoderReportId(UUID.fromString(raw))

        fun fromOrNull(raw: String): CoderReportId? = runCatching { CoderReportId(UUID.fromString(raw)) }.getOrNull()
    }
}
