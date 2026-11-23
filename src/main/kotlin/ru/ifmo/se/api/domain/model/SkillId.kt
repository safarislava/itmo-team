package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class SkillId(
    val value: UUID,
) {
    override fun toString(): String = value.toString()

    companion object {
        fun generate(): SkillId = SkillId(uuidV7())

        fun from(raw: String): SkillId = SkillId(UUID.fromString(raw))

        fun fromOrNull(raw: String): SkillId? = runCatching { SkillId(UUID.fromString(raw)) }.getOrNull()
    }
}
