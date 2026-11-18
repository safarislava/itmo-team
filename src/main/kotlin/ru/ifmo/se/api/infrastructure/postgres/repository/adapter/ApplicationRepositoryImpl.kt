package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Repository
import ru.ifmo.se.api.domain.exception.ApplicationBadRequestException
import ru.ifmo.se.api.domain.model.Application
import ru.ifmo.se.api.domain.model.ApplicationFilter
import ru.ifmo.se.api.domain.model.ApplicationId
import ru.ifmo.se.api.domain.model.ApplicationStatus
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.SortDirection
import ru.ifmo.se.api.domain.model.VacancyId
import ru.ifmo.se.api.domain.repository.ApplicationRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.ApplicationEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.PersonEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.TeamEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.VacancyEntity
import ru.ifmo.se.api.infrastructure.postgres.mapper.ApplicationEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaApplicationRepository
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaFormRepository
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaPersonRepository
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaResumeRepository
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaVacancyRepository
import java.util.UUID

@Repository
class ApplicationRepositoryImpl(
    private val repo: JpaApplicationRepository,
    private val mapper: ApplicationEntityMapper,
    private val personRepo: JpaPersonRepository,
    private val vacancyRepo: JpaVacancyRepository,
    private val resumeRepo: JpaResumeRepository,
    private val formRepo: JpaFormRepository,
) : ApplicationRepository {
    override fun findById(id: ApplicationId): Application? = repo.findById(id.value).map(mapper::toDomain).orElse(null)

    override fun findByCandidateIdAndVacancyId(
        candidateId: PersonId,
        vacancyId: VacancyId,
    ): Application? = repo.findByCandidateIdAndVacancyId(candidateId.value, vacancyId.value)?.let(mapper::toDomain)

    override fun findAll(
        filter: ApplicationFilter,
        pageQuery: PageQuery,
    ): PageResult<Application> {
        val specification =
            Specification<ApplicationEntity> { root, _, cb ->
                val predicates = mutableListOf<Predicate>()

                filter.vacancyId?.let {
                    predicates.add(cb.equal(root.get<VacancyEntity>("vacancy").get<UUID>("id"), it.value))
                }

                filter.candidateId?.let {
                    predicates.add(cb.equal(root.get<PersonEntity>("candidate").get<UUID>("id"), it.value))
                }

                filter.status?.let {
                    predicates.add(cb.equal(root.get<ApplicationStatus>("status"), it))
                }

                filter.teamId?.let {
                    predicates.add(
                        cb.equal(
                            root.get<VacancyEntity>("vacancy").get<TeamEntity>("team").get<UUID>("id"),
                            it.value,
                        ),
                    )
                }

                filter.companyId?.let {
                    predicates.add(
                        cb.equal(
                            root
                                .get<VacancyEntity>("vacancy")
                                .get<TeamEntity>("team")
                                .get<ru.ifmo.se.api.infrastructure.postgres.entity.CompanyEntity>("company")
                                .get<UUID>("id"),
                            it.value,
                        ),
                    )
                }

                cb.and(*predicates.toTypedArray())
            }

        val direction = if (pageQuery.sortOrder == SortDirection.DESC) Sort.Direction.DESC else Sort.Direction.ASC
        val sortProperty =
            when (pageQuery.sortBy) {
                "status" -> "status"
                else -> "id"
            }

        val pageable = PageRequest.of(pageQuery.pageNumber, pageQuery.pageSize, Sort.by(direction, sortProperty))
        val pagedResult = repo.findAll(specification, pageable)

        return PageResult(
            items = pagedResult.content.map(mapper::toDomain),
            totalElements = pagedResult.totalElements,
            pageNumber = pagedResult.number,
            totalPages = pagedResult.totalPages,
        )
    }

    override fun save(application: Application): Application {
        val existing = repo.findById(application.id.value).orElse(null)
        val entity =
            if (existing != null) {
                existing.status = application.status
                existing
            } else {
                val candidate =
                    personRepo.findById(application.candidateId.value).orElseThrow {
                        ApplicationBadRequestException("Candidate not found.")
                    }
                val vacancy =
                    vacancyRepo.findById(application.vacancyId.value).orElseThrow {
                        ApplicationBadRequestException("Vacancy not found.")
                    }
                val resume =
                    resumeRepo.findById(application.resumeId.value).orElseThrow {
                        ApplicationBadRequestException("Resume not found.")
                    }
                val form =
                    formRepo.findById(application.formId.value).orElseThrow {
                        ApplicationBadRequestException("Form not found.")
                    }

                ApplicationEntity(
                    id = application.id.value,
                    candidate = candidate,
                    vacancy = vacancy,
                    resume = resume,
                    form = form,
                    status = application.status,
                )
            }

        return mapper.toDomain(repo.save(entity))
    }

    override fun delete(application: Application) {
        repo.deleteById(application.id.value)
    }
}
