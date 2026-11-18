package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import ru.ifmo.se.api.domain.model.ApplicationId
import ru.ifmo.se.api.domain.model.ApplicationResponse
import ru.ifmo.se.api.domain.model.ApplicationResponseId
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.repository.ApplicationResponseRepository
import ru.ifmo.se.api.infrastructure.postgres.mapper.ApplicationResponseEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaApplicationResponseRepository
import java.time.Instant

@Repository
class ApplicationResponseRepositoryImpl(
    private val repo: JpaApplicationResponseRepository,
    private val mapper: ApplicationResponseEntityMapper,
) : ApplicationResponseRepository {
    override fun findById(id: ApplicationResponseId): ApplicationResponse? = repo.findById(id.value).map(mapper::toDomain).orElse(null)

    override fun findAllByApplicationId(applicationId: ApplicationId): List<ApplicationResponse> =
        repo.findAllByApplicationId(applicationId.value).map(mapper::toDomain)

    override fun countByApplicationId(applicationId: ApplicationId): Long = repo.countByApplicationId(applicationId.value)

    override fun findNextApplicationForScreening(
        companyId: CompanyId,
        hrId: PersonId,
    ): ApplicationId? {
        val candidates =
            repo.findCandidatesForScreening(
                companyId = companyId.value,
                hrId = hrId.value,
                now = Instant.now(),
                pageable = PageRequest.of(0, 1),
            )
        return candidates.firstOrNull()?.let { ApplicationId(it) }
    }

    override fun save(response: ApplicationResponse): ApplicationResponse = mapper.toDomain(repo.save(mapper.toEntity(response)))

    override fun delete(response: ApplicationResponse) {
        repo.deleteById(response.id.value)
    }
}
