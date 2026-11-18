package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.ApplicationId
import ru.ifmo.se.api.domain.model.ApplicationResponse
import ru.ifmo.se.api.domain.model.ApplicationResponseId

interface ApplicationResponseRepository {
    fun findById(id: ApplicationResponseId): ApplicationResponse?

    fun findAllByApplicationId(applicationId: ApplicationId): List<ApplicationResponse>

    fun countByApplicationId(applicationId: ApplicationId): Long

    fun save(response: ApplicationResponse): ApplicationResponse

    fun delete(response: ApplicationResponse)
}
