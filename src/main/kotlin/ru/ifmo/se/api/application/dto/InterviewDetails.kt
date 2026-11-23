package ru.ifmo.se.api.application.dto

import ru.ifmo.se.api.domain.model.ApplicationId
import ru.ifmo.se.api.domain.model.Interview
import ru.ifmo.se.api.domain.model.InterviewFeedback
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.Vacancy

data class InterviewDetails(
    val interview: Interview,
    val applicationId: ApplicationId,
    val candidate: Person,
    val teamlead: Person,
    val vacancy: Vacancy,
    val feedback: InterviewFeedback? = null,
)
