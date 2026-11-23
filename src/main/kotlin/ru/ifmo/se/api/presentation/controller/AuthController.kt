package ru.ifmo.se.api.presentation.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.ifmo.se.api.application.dto.LoginCommand
import ru.ifmo.se.api.application.dto.RegisterCommand
import ru.ifmo.se.api.application.port.out.AccessTokenPolicy
import ru.ifmo.se.api.application.port.out.RefreshTokenPolicy
import ru.ifmo.se.api.application.service.AuthService
import ru.ifmo.se.api.domain.exception.InvalidRefreshTokenException
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.presentation.dto.request.LoginRequest
import ru.ifmo.se.api.presentation.dto.request.RegisterRequest
import ru.ifmo.se.api.presentation.dto.response.PersonResponse
import java.time.Duration

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val accessTokenPolicy: AccessTokenPolicy,
    private val refreshTokenPolicy: RefreshTokenPolicy,
) {
    @PostMapping("/accounts")
    fun createAccount(
        @Valid @RequestBody request: RegisterRequest,
        httpRequest: HttpServletRequest,
    ): ResponseEntity<PersonResponse> {
        val command =
            RegisterCommand(
                name = request.name,
                email = request.email,
                password = request.password,
            )
        val result = authService.register(command)
        val accessCookie = createAccessTokenCookie(result.accessToken, accessTokenPolicy.expirationSeconds, httpRequest.isSecure)
        val refreshCookie = createRefreshCookie(result.refreshToken, refreshTokenPolicy.expirationSeconds, httpRequest.isSecure)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(toPersonResponse(result.person))
    }

    @PostMapping("/sessions")
    fun createSession(
        @Valid @RequestBody request: LoginRequest,
        httpRequest: HttpServletRequest,
    ): ResponseEntity<PersonResponse> {
        val command =
            LoginCommand(
                email = request.email,
                password = request.password,
            )
        val result = authService.login(command)
        val accessCookie = createAccessTokenCookie(result.accessToken, accessTokenPolicy.expirationSeconds, httpRequest.isSecure)
        val refreshCookie = createRefreshCookie(result.refreshToken, refreshTokenPolicy.expirationSeconds, httpRequest.isSecure)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(toPersonResponse(result.person))
    }

    @DeleteMapping("/sessions/current")
    fun deleteSession(
        @CookieValue(name = "refreshToken", required = false) refreshToken: String?,
        httpRequest: HttpServletRequest,
    ): ResponseEntity<Void> {
        authService.logout(refreshToken)

        val expiredAccessCookie = createAccessTokenCookie("", 0, httpRequest.isSecure)
        val expiredRefreshCookie = createRefreshCookie("", 0, httpRequest.isSecure)

        return ResponseEntity
            .noContent()
            .header(HttpHeaders.SET_COOKIE, expiredAccessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, expiredRefreshCookie.toString())
            .build()
    }

    @PostMapping("/sessions/current/tokens")
    fun createSessionTokens(
        @CookieValue(name = "refreshToken", required = false) refreshToken: String?,
        httpRequest: HttpServletRequest,
    ): ResponseEntity<PersonResponse> {
        val token = refreshToken ?: throw InvalidRefreshTokenException()
        val result = authService.refreshToken(token)
        val accessCookie = createAccessTokenCookie(result.accessToken, accessTokenPolicy.expirationSeconds, httpRequest.isSecure)
        val refreshCookie = createRefreshCookie(result.refreshToken, refreshTokenPolicy.expirationSeconds, httpRequest.isSecure)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(toPersonResponse(result.person))
    }

    private fun toPersonResponse(person: Person): PersonResponse =
        PersonResponse(
            id = person.id.value,
            name = person.name,
            email = person.email,
            role = person.role,
            company = null,
            team = null,
            githubUsername = person.githubUsername,
        )

    private fun createAccessTokenCookie(
        token: String,
        maxAgeSeconds: Long,
        isSecure: Boolean,
    ): ResponseCookie =
        ResponseCookie
            .from("accessToken", token)
            .httpOnly(true)
            .secure(isSecure)
            .path("/")
            .maxAge(Duration.ofSeconds(maxAgeSeconds))
            .sameSite("Strict")
            .build()

    private fun createRefreshCookie(
        token: String,
        maxAgeSeconds: Long,
        isSecure: Boolean,
    ): ResponseCookie =
        ResponseCookie
            .from("refreshToken", token)
            .httpOnly(true)
            .secure(isSecure)
            .path("/api/auth")
            .maxAge(Duration.ofSeconds(maxAgeSeconds))
            .sameSite("Strict")
            .build()
}
