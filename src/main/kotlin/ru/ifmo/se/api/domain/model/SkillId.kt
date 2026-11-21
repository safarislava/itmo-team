package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class SkillId(
    val value: UUID,
) {
    companion object {
        fun generate(): SkillId = SkillId(UUID.randomUUID())
    }
}
