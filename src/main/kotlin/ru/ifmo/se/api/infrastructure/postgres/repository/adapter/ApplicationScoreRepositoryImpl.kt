package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import jakarta.persistence.EntityManager
import jakarta.persistence.Tuple
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Expression
import jakarta.persistence.criteria.From
import jakarta.persistence.criteria.JoinType
import jakarta.persistence.criteria.Predicate
import org.springframework.stereotype.Repository
import ru.ifmo.se.api.domain.model.ApplicationId
import ru.ifmo.se.api.domain.model.ApplicationScore
import ru.ifmo.se.api.domain.model.ApplicationStatus
import ru.ifmo.se.api.domain.model.FormId
import ru.ifmo.se.api.domain.model.LeaderboardEntry
import ru.ifmo.se.api.domain.model.LeaderboardFilter
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.ResumeId
import ru.ifmo.se.api.domain.model.VacancyId
import ru.ifmo.se.api.domain.repository.ApplicationScoreRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.ApplicationEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.ApplicationScoreEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.FormEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.PersonEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.ResumeEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.VacancyEntity
import ru.ifmo.se.api.infrastructure.postgres.mapper.ApplicationScoreEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaApplicationScoreRepository
import java.util.UUID
import kotlin.math.ceil

@Repository
class ApplicationScoreRepositoryImpl(
    private val repo: JpaApplicationScoreRepository,
    private val mapper: ApplicationScoreEntityMapper,
    private val entityManager: EntityManager,
) : ApplicationScoreRepository {
    override fun findByApplicationId(applicationId: ApplicationId): ApplicationScore? =
        repo.findByApplicationId(applicationId.value)?.let(mapper::toDomain)

    override fun getLeaderboard(
        filter: LeaderboardFilter,
        pageQuery: PageQuery,
    ): PageResult<LeaderboardEntry> {
        val cb = entityManager.criteriaBuilder

        val countQuery = cb.createQuery(Long::class.java)
        val countScore = countQuery.from(ApplicationScoreEntity::class.java)
        val countApp = countScore.join<ApplicationScoreEntity, ApplicationEntity>("application", JoinType.INNER)
        val countForm = countApp.join<ApplicationEntity, FormEntity>("form", JoinType.INNER)
        val countVacancy = countApp.join<ApplicationEntity, VacancyEntity>("vacancy", JoinType.INNER)

        val countScoreExpr = buildScoreExpression(cb, countScore, filter)
        val countPredicates = buildPredicates(cb, countApp, countScoreExpr, countForm, countVacancy, filter)

        countQuery.select(cb.count(countScore))
        countQuery.where(*countPredicates.toTypedArray())

        val totalElements = entityManager.createQuery(countQuery).singleResult ?: 0L
        if (totalElements == 0L) {
            return PageResult.empty(pageQuery.pageNumber)
        }

        val query = cb.createQuery(Tuple::class.java)
        val rootScore = query.from(ApplicationScoreEntity::class.java)
        val appJoin = rootScore.join<ApplicationScoreEntity, ApplicationEntity>("application", JoinType.INNER)
        val candidateJoin = appJoin.join<ApplicationEntity, PersonEntity>("candidate", JoinType.INNER)
        val vacancyJoin = appJoin.join<ApplicationEntity, VacancyEntity>("vacancy", JoinType.INNER)
        val formJoin = appJoin.join<ApplicationEntity, FormEntity>("form", JoinType.INNER)
        val resumeJoin = appJoin.join<ApplicationEntity, ResumeEntity>("resume", JoinType.INNER)

        val scoreExpr = buildScoreExpression(cb, rootScore, filter)
        val predicates = buildPredicates(cb, appJoin, scoreExpr, formJoin, vacancyJoin, filter)

        query.select(
            cb.tuple(
                appJoin.get<UUID>("id"),
                candidateJoin.get<UUID>("id"),
                candidateJoin.get<String>("name"),
                vacancyJoin.get<UUID>("id"),
                resumeJoin.get<UUID>("id"),
                formJoin.get<UUID>("id"),
                appJoin.get<ApplicationStatus>("status"),
                scoreExpr,
                rootScore.get<Short>("motivation"),
                rootScore.get<Short>("softSkills"),
                rootScore.get<Short>("petProjects"),
                rootScore.get<Short>("jobExp"),
                rootScore.get<Short>("hardSkills"),
                rootScore.get<Short>("workFormat"),
            ),
        )
        query.where(*predicates.toTypedArray())
        query.orderBy(cb.desc(scoreExpr), cb.asc(appJoin.get<UUID>("id")))

        val typedQuery = entityManager.createQuery(query)
        typedQuery.firstResult = pageQuery.pageNumber * pageQuery.pageSize
        typedQuery.maxResults = pageQuery.pageSize

        val tuples = typedQuery.resultList
        val items =
            tuples.map { tuple ->
                val appId = tuple.get(0, UUID::class.java)
                val candidateId = tuple.get(1, UUID::class.java)
                val candidateName = tuple.get(2, String::class.java)
                val vacancyId = tuple.get(3, UUID::class.java)
                val resumeId = tuple.get(4, UUID::class.java)
                val formId = tuple.get(5, UUID::class.java)
                val status = tuple.get(6, ApplicationStatus::class.java)
                val score = (tuple.get(7) as Number).toDouble()

                val scoreMetrics =
                    ApplicationScore(
                        applicationId = ApplicationId(appId),
                        motivation = (tuple.get(8) as Number).toShort(),
                        softSkills = (tuple.get(9) as Number).toShort(),
                        petProjects = (tuple.get(10) as Number).toShort(),
                        jobExp = (tuple.get(11) as Number).toShort(),
                        hardSkills = (tuple.get(12) as Number).toShort(),
                        workFormat = (tuple.get(13) as Number).toShort(),
                    )

                LeaderboardEntry(
                    applicationId = ApplicationId(appId),
                    candidateId = PersonId(candidateId),
                    candidateName = candidateName,
                    vacancyId = VacancyId(vacancyId),
                    resumeId = ResumeId(resumeId),
                    formId = FormId(formId),
                    status = status,
                    score = score,
                    scoreMetrics = scoreMetrics,
                )
            }

        val totalPages = ceil(totalElements.toDouble() / pageQuery.pageSize).toInt()

        return PageResult(
            items = items,
            totalElements = totalElements,
            pageNumber = pageQuery.pageNumber,
            totalPages = totalPages,
        )
    }

    private fun buildScoreExpression(
        cb: CriteriaBuilder,
        score: From<*, ApplicationScoreEntity>,
        filter: LeaderboardFilter,
    ): Expression<Double> {
        val motivation = score.get<Number>("motivation").`as`(Double::class.javaObjectType)
        val softSkills = score.get<Number>("softSkills").`as`(Double::class.javaObjectType)
        val petProjects = score.get<Number>("petProjects").`as`(Double::class.javaObjectType)
        val jobExp = score.get<Number>("jobExp").`as`(Double::class.javaObjectType)
        val hardSkills = score.get<Number>("hardSkills").`as`(Double::class.javaObjectType)
        val workFormat = score.get<Number>("workFormat").`as`(Double::class.javaObjectType)

        return cb.sum(
            cb.sum(
                cb.sum(
                    cb.prod(motivation, filter.motivationWeight),
                    cb.prod(softSkills, filter.softSkillsWeight),
                ),
                cb.sum(
                    cb.prod(petProjects, filter.petProjectsWeight),
                    cb.prod(jobExp, filter.jobExpWeight),
                ),
            ),
            cb.sum(
                cb.prod(hardSkills, filter.hardSkillsWeight),
                cb.prod(workFormat, filter.workFormatWeight),
            ),
        )
    }

    private fun buildPredicates(
        cb: CriteriaBuilder,
        app: From<*, ApplicationEntity>,
        scoreExpr: Expression<Double>,
        form: From<*, FormEntity>,
        vacancy: From<*, VacancyEntity>,
        filter: LeaderboardFilter,
    ): List<Predicate> {
        val predicates = mutableListOf<Predicate>()
        predicates.add(cb.equal(vacancy.get<UUID>("id"), filter.vacancyId.value))

        if (filter.status != null) {
            predicates.add(cb.equal(app.get<ApplicationStatus>("status"), filter.status))
        }

        if (filter.minScore != null) {
            predicates.add(cb.greaterThanOrEqualTo(scoreExpr, filter.minScore))
        }

        filter.skillIds?.takeIf { it.isNotEmpty() }?.forEach { skillId ->
            predicates.add(
                cb.isNotNull(
                    cb.function(
                        "array_position",
                        Int::class.javaObjectType,
                        form.get<List<UUID>>("skillIds"),
                        cb.literal(skillId.value),
                    ),
                ),
            )
        }

        return predicates
    }
}
