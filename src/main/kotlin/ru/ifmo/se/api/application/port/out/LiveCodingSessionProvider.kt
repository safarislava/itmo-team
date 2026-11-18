package ru.ifmo.se.api.application.port.out

import ru.ifmo.se.api.domain.model.InterviewId
import java.time.Instant

interface LiveCodingSessionProvider {
    fun createSession(
        interviewId: InterviewId,
        expiresAt: Instant,
    ): LiveCodingSessionCredentials
}

data class LiveCodingSessionCredentials(
    val interviewId: InterviewId,
    val websocketUrl: String,
    val token: String,
    val expiresAt: Instant,
    val iceServers: List<IceServerConfig>,
)

data class IceServerConfig(
    val urls: List<String>,
    val username: String? = null,
    val credential: String? = null,
)
