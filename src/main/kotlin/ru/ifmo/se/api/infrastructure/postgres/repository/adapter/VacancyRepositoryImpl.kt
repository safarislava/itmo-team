package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.ContestId
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.Vacancy
import ru.ifmo.se.api.domain.model.VacancyFilter
import ru.ifmo.se.api.domain.model.VacancyId
import ru.ifmo.se.api.domain.model.VacancySummary
import ru.ifmo.se.api.domain.repository.VacancyRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.CompanyEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.TeamEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.VacancyEntity
import ru.ifmo.se.api.infrastructure.postgres.extension.toPageRequest
import ru.ifmo.se.api.infrastructure.postgres.extension.toPageResult
import ru.ifmo.se.api.infrastructure.postgres.mapper.VacancyEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaVacancyRepository
import java.util.UUID

@Repository
class VacancyRepositoryImpl(
    private val repo: JpaVacancyRepository,
    private val mapper: VacancyEntityMapper,
) : VacancyRepository {
    override fun findById(id: VacancyId): Vacancy? = repo.findById(id.value).map(mapper::toDomain).orElse(null)

    override fun findAllById(ids: Iterable<VacancyId>): List<Vacancy> =
        ids.map { it.value }.takeIf { it.isNotEmpty() }?.let { repo.findAllById(it).map(mapper::toDomain) } ?: emptyList()

    override fun findAllSummaries(
        filter: VacancyFilter,
        pageQuery: PageQuery,
    ): PageResult<VacancySummary> {
        val spec = buildSpecification(filter)
        val pageable = toPageable(pageQuery)
        val page = repo.findAllSummaries(spec, pageable)
        return page.toPageResult { projection ->
            VacancySummary(
                id = VacancyId(projection.id),
                title = projection.title,
                city = projection.city,
                salary = projection.salary,
                status = projection.status,
                createdAt = projection.createdAt,
                companyId = CompanyId(projection.companyId),
                companyName = projection.companyName,
            )
        }
    }

    private fun toPageable(pageQuery: PageQuery) =
        pageQuery.toPageRequest {
            when (it) {
                "id" -> "id"
                "title" -> "title"
                "salary" -> "salary"
                else -> "createdAt"
            }
        }

    private fun buildSpecification(filter: VacancyFilter): Specification<VacancyEntity> =
        Specification { root, _, cb ->
            val predicates = mutableListOf<Predicate>()

            filter.search?.takeIf { it.isNotBlank() }?.let { query ->
                val pattern = "%${query.trim().lowercase()}%"
                predicates.add(cb.like(cb.lower(root.get("title")), pattern))
            }

            filter.city?.takeIf { it.isNotBlank() }?.let { city ->
                predicates.add(cb.equal(root.get<String>("city"), city.trim()))
            }

            filter.minSalary?.let { minSalary ->
                predicates.add(cb.greaterThanOrEqualTo(root.get("salary"), minSalary))
            }

            filter.status?.let { status ->
                predicates.add(cb.equal(root.get<Any>("status"), status))
            }

            filter.teamId?.let { teamId ->
                predicates.add(cb.equal(root.get<TeamEntity>("team").get<UUID>("id"), teamId.value))
            }

            filter.companyId?.let { companyId ->
                predicates.add(
                    cb.equal(
                        root.get<TeamEntity>("team").get<CompanyEntity>("company").get<UUID>("id"),
                        companyId.value,
                    ),
                )
            }

            filter.skillIds?.takeIf { it.isNotEmpty() }?.forEach { skillId ->
                predicates.add(
                    cb.isNotNull(
                        cb.function(
                            "array_position",
                            Int::class.javaObjectType,
                            root.get<List<UUID>>("skillIds"),
                            cb.literal(skillId.value),
                        ),
                    ),
                )
            }

            if (predicates.isEmpty()) cb.conjunction() else cb.and(*predicates.toTypedArray())
        }

    @Transactional
    override fun save(vacancy: Vacancy): Vacancy = mapper.toDomain(repo.save(mapper.toEntity(vacancy)))

    @Transactional
    override fun delete(id: VacancyId) = repo.deleteById(id.value)

    @Transactional
    override fun attachContest(
        vacancyId: VacancyId,
        contestId: ContestId,
    ) = repo.attachContest(vacancyId.value, contestId.value)

    @Transactional
    override fun detachContest(
        vacancyId: VacancyId,
        contestId: ContestId,
    ) = repo.detachContest(vacancyId.value, contestId.value)

    override fun isContestAttached(
        vacancyId: VacancyId,
        contestId: ContestId,
    ): Boolean = repo.isContestAttached(vacancyId.value, contestId.value)

    override fun countContests(vacancyId: VacancyId): Long = repo.countContestsByVacancyId(vacancyId.value)
}
