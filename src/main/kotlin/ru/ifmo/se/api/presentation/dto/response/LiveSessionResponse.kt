package ru.ifmo.se.api.presentation.dto.response

import java.time.Instant
import java.util.UUID

data class LiveSessionResponse(
    val interviewId: UUID,
    val websocketUrl: String,
    val token: String,
    val expiresAt: Instant,
    val iceServers: List<IceServerResponse>,
)

data class IceServerResponse(
    val urls: List<String>,
    val username: String? = null,
    val credential: String? = null,
)
