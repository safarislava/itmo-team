package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.RefreshToken

interface RefreshTokenRepository {
    fun findByTokenHash(tokenHash: String): RefreshToken?

    fun consumeByTokenHash(tokenHash: String): RefreshToken?

    fun save(token: RefreshToken): RefreshToken

    fun revokeAllByPersonId(personId: PersonId)
}
