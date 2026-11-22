package ru.ifmo.se.api.presentation.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.ApplicationScore
import ru.ifmo.se.api.presentation.dto.response.ScoreMetricsResponse

@Component
class ScoreMetricsResponseMapper {
    fun toResponse(domain: ApplicationScore): ScoreMetricsResponse =
        ScoreMetricsResponse(
            motivation = domain.motivation,
            softSkills = domain.softSkills,
            petProjects = domain.petProjects,
            jobExp = domain.jobExp,
            hardSkills = domain.hardSkills,
            workFormat = domain.workFormat,
        )
}
