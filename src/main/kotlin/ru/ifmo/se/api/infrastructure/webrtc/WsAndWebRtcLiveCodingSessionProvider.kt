package ru.ifmo.se.api.infrastructure.webrtc

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service
import ru.ifmo.se.api.application.port.out.IceServerConfig
import ru.ifmo.se.api.application.port.out.LiveCodingSessionCredentials
import ru.ifmo.se.api.application.port.out.LiveCodingSessionProvider
import ru.ifmo.se.api.domain.model.InterviewId
import ru.ifmo.se.api.domain.model.uuidV7
import java.time.Instant

@Service
@ConfigurationProperties(prefix = "app.live-coding")
class WsAndWebRtcLiveCodingSessionProvider : LiveCodingSessionProvider {
    lateinit var wsUrlTemplate: String
    lateinit var iceServers: List<IceServerProperties>

    override fun createSession(
        interviewId: InterviewId,
        expiresAt: Instant,
    ): LiveCodingSessionCredentials {
        val token = uuidV7().toString()
        val websocketUrl = wsUrlTemplate.format(interviewId.value)
        val serverConfigs =
            iceServers.map {
                IceServerConfig(
                    urls = it.urls,
                    username = it.username,
                    credential = it.credential,
                )
            }

        return LiveCodingSessionCredentials(
            interviewId = interviewId,
            websocketUrl = websocketUrl,
            token = token,
            expiresAt = expiresAt,
            iceServers = serverConfigs,
        )
    }

    class IceServerProperties {
        lateinit var urls: List<String>
        var username: String? = null
        var credential: String? = null
    }
}
