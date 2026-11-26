package ru.ifmo.se.api.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.application.annotation.ReadOnlyDataAccess
import ru.ifmo.se.api.application.annotation.WriteDataAccess
import ru.ifmo.se.api.application.dto.AuthResult
import ru.ifmo.se.api.application.dto.LoginCommand
import ru.ifmo.se.api.application.dto.RegisterCommand
import ru.ifmo.se.api.application.port.out.AccessTokenGenerator
import ru.ifmo.se.api.application.port.out.PasswordHasher
import ru.ifmo.se.api.application.port.out.RefreshTokenGenerator
import ru.ifmo.se.api.application.port.out.RefreshTokenPolicy
import ru.ifmo.se.api.application.port.out.TokenHasher
import ru.ifmo.se.api.domain.exception.InvalidCredentialsException
import ru.ifmo.se.api.domain.exception.InvalidRefreshTokenException
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.RefreshToken
import ru.ifmo.se.api.domain.repository.PersonRepository
import ru.ifmo.se.api.domain.repository.RefreshTokenRepository
import java.time.Instant

@Service
class AuthService(
    private val personRepository: PersonRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val accessTokenGenerator: AccessTokenGenerator,
    private val refreshTokenGenerator: RefreshTokenGenerator,
    private val refreshTokenPolicy: RefreshTokenPolicy,
    private val tokenHasher: TokenHasher,
    private val passwordHasher: PasswordHasher,
) {
    @WriteDataAccess
    @Transactional
    fun register(command: RegisterCommand): AuthResult {
        val person =
            Person(
                id = PersonId.generate(),
                name = command.name,
                email = command.email,
                passwordHash = passwordHasher.hash(command.password),
                role = PersonRole.CANDIDATE,
                companyId = null,
                teamId = null,
                githubUsername = null,
            )

        val savedPerson = personRepository.save(person)
        return issueTokens(savedPerson)
    }

    @ReadOnlyDataAccess
    fun login(command: LoginCommand): AuthResult {
        val person =
            personRepository.findByEmail(command.email)
                ?: throw InvalidCredentialsException()

        if (!passwordHasher.matches(command.password, person.passwordHash)) {
            throw InvalidCredentialsException()
        }

        return issueTokens(person)
    }

    @ReadOnlyDataAccess
    fun refreshToken(refreshToken: String): AuthResult {
        val hash = tokenHasher.hash(refreshToken)
        val token =
            refreshTokenRepository.consumeByTokenHash(hash)
                ?: throw InvalidRefreshTokenException()

        if (!token.isValid()) {
            throw InvalidRefreshTokenException()
        }

        val person =
            personRepository.findById(token.personId)
                ?: throw InvalidRefreshTokenException()

        return issueTokens(person)
    }

    fun logout(refreshToken: String?) {
        if (refreshToken.isNullOrBlank()) {
            throw InvalidRefreshTokenException()
        }

        val hash = tokenHasher.hash(refreshToken)
        val token =
            refreshTokenRepository.consumeByTokenHash(hash)
                ?: throw InvalidRefreshTokenException()

        if (token.revoked) {
            throw InvalidRefreshTokenException()
        }
    }

    private fun issueTokens(person: Person): AuthResult {
        val accessToken = accessTokenGenerator.generate(person)
        val refreshToken = refreshTokenGenerator.generate()
        val hash = tokenHasher.hash(refreshToken)
        val now = Instant.now()
        val expiresAt = now.plusSeconds(refreshTokenPolicy.expirationSeconds)

        val token =
            RefreshToken(
                personId = person.id,
                tokenHash = hash,
                expiresAt = expiresAt,
                createdAt = now,
                revoked = false,
            )

        refreshTokenRepository.save(token)

        return AuthResult(
            accessToken = accessToken,
            refreshToken = refreshToken,
            person = person,
        )
    }
}
