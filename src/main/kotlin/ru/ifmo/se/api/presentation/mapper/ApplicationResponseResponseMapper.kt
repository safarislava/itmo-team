package ru.ifmo.se.api.presentation.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.application.dto.ApplicationResponseDetails
import ru.ifmo.se.api.presentation.dto.response.ApplicationResponseResponse
import ru.ifmo.se.api.presentation.dto.response.PersonSummaryResponse

@Component
class ApplicationResponseResponseMapper {
    fun toResponse(details: ApplicationResponseDetails): ApplicationResponseResponse =
        ApplicationResponseResponse(
            id = details.response.id.value,
            applicationId = details.response.applicationId.value,
            hr =
                PersonSummaryResponse(
                    id = details.hr.id.value,
                    name = details.hr.name,
                    role = details.hr.role,
                ),
            motivation = details.response.motivation,
            softSkills = details.response.softSkills,
            petProjects = details.response.petProjects,
            jobExp = details.response.jobExp,
            hardSkills = details.response.hardSkills,
            workFormat = details.response.workFormat,
        )
}
