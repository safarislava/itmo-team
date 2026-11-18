package ru.ifmo.se.api.application.dto

import ru.ifmo.se.api.domain.model.ApplicationId
import ru.ifmo.se.api.domain.model.InterviewStatus
import java.time.Instant

data class ScheduleInterviewCommand(
    val applicationId: ApplicationId,
    val scheduledStart: Instant,
    val scheduledEnd: Instant,
)

data class UpdateInterviewStatusCommand(
    val status: InterviewStatus,
)
