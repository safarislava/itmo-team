package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.ApplicationId
import ru.ifmo.se.api.domain.model.ApplicationResponse
import ru.ifmo.se.api.domain.model.ApplicationResponseId
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.PersonId

interface ApplicationResponseRepository {
    fun findById(id: ApplicationResponseId): ApplicationResponse?

    fun findAllByApplicationId(applicationId: ApplicationId): List<ApplicationResponse>

    fun countByApplicationId(applicationId: ApplicationId): Long

    fun findNextApplicationForScreening(
        companyId: CompanyId,
        hrId: PersonId,
    ): ApplicationId?

    fun save(response: ApplicationResponse): ApplicationResponse

    fun delete(response: ApplicationResponse)
}
