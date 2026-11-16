package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.RefreshToken
import ru.ifmo.se.api.domain.model.RefreshTokenId

interface RefreshTokenRepository {
    fun findById(id: RefreshTokenId): RefreshToken?

    fun findByTokenHash(tokenHash: String): RefreshToken?

    fun findAllByPersonId(personId: PersonId): List<RefreshToken>

    fun save(token: RefreshToken): RefreshToken

    fun delete(token: RefreshToken)

    fun deleteById(id: RefreshTokenId): Boolean

    fun revokeAllByPersonId(personId: PersonId)
}
