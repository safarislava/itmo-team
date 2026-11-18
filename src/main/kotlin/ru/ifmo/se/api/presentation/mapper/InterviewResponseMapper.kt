package ru.ifmo.se.api.presentation.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.application.dto.InterviewDetails
import ru.ifmo.se.api.application.port.out.LiveCodingSessionCredentials
import ru.ifmo.se.api.presentation.dto.response.IceServerResponse
import ru.ifmo.se.api.presentation.dto.response.InterviewResponse
import ru.ifmo.se.api.presentation.dto.response.LiveSessionResponse
import ru.ifmo.se.api.presentation.dto.response.VacancySummaryResponse

@Component
class InterviewResponseMapper(
    private val personResponseMapper: PersonResponseMapper,
    private val interviewFeedbackResponseMapper: InterviewFeedbackResponseMapper,
) {
    fun toResponse(details: InterviewDetails): InterviewResponse =
        InterviewResponse(
            id = details.interview.id.value,
            applicationId = details.applicationId.value,
            candidate = personResponseMapper.toSummaryResponse(details.candidate),
            teamlead = personResponseMapper.toSummaryResponse(details.teamlead),
            vacancy =
                VacancySummaryResponse(
                    id = details.vacancy.id,
                    title = details.vacancy.title,
                    status = details.vacancy.status,
                ),
            scheduledStart = details.interview.scheduledStart,
            scheduledEnd = details.interview.scheduledEnd,
            status = details.interview.status,
            hasRecording = details.interview.videoS3key != null,
            feedback = details.feedback?.let(interviewFeedbackResponseMapper::toResponse),
        )

    fun toLiveSessionResponse(credentials: LiveCodingSessionCredentials): LiveSessionResponse =
        LiveSessionResponse(
            interviewId = credentials.interviewId.value,
            websocketUrl = credentials.websocketUrl,
            token = credentials.token,
            expiresAt = credentials.expiresAt,
            iceServers =
                credentials.iceServers.map {
                    IceServerResponse(
                        urls = it.urls,
                        username = it.username,
                        credential = it.credential,
                    )
                },
        )
}
