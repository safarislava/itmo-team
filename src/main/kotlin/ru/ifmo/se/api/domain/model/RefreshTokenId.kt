package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class RefreshTokenId(
    val value: UUID,
) {
    override fun toString(): String = value.toString()

    companion object {
        fun generate(): RefreshTokenId = RefreshTokenId(uuidV7())

        fun from(raw: String): RefreshTokenId = RefreshTokenId(UUID.fromString(raw))

        fun fromOrNull(raw: String): RefreshTokenId? = runCatching { RefreshTokenId(UUID.fromString(raw)) }.getOrNull()
    }
}
