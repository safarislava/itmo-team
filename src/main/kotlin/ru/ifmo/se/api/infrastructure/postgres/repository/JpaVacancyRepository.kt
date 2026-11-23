package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.ifmo.se.api.domain.model.VacancyStatus
import ru.ifmo.se.api.infrastructure.postgres.entity.VacancyEntity
import ru.ifmo.se.api.infrastructure.postgres.projection.VacancySummaryProjection
import java.time.Instant
import java.util.UUID

interface JpaVacancyRepository :
    JpaRepository<VacancyEntity, UUID>,
    JpaSpecificationExecutor<VacancyEntity> {
    @Modifying
    @Query(
        value = "UPDATE vacancy SET skill_ids = array_remove(skill_ids, :skillId) WHERE :skillId = ANY(skill_ids)",
        nativeQuery = true,
    )
    fun removeSkillIdFromAll(
        @Param("skillId") skillId: UUID,
    )

    @Modifying
    @Query(
        value = "INSERT INTO vacancy_contest (vacancy_id, contest_id) VALUES (:vacancyId, :contestId) ON CONFLICT DO NOTHING",
        nativeQuery = true,
    )
    fun attachContest(
        @Param("vacancyId") vacancyId: UUID,
        @Param("contestId") contestId: UUID,
    )

    @Modifying
    @Query(
        value = "DELETE FROM vacancy_contest WHERE vacancy_id = :vacancyId AND contest_id = :contestId",
        nativeQuery = true,
    )
    fun detachContest(
        @Param("vacancyId") vacancyId: UUID,
        @Param("contestId") contestId: UUID,
    )

    @Query(
        value = "SELECT COUNT(*) > 0 FROM vacancy_contest WHERE vacancy_id = :vacancyId AND contest_id = :contestId",
        nativeQuery = true,
    )
    fun isContestAttached(
        @Param("vacancyId") vacancyId: UUID,
        @Param("contestId") contestId: UUID,
    ): Boolean

    @Query(
        value = "SELECT COUNT(*) FROM vacancy_contest WHERE vacancy_id = :vacancyId",
        nativeQuery = true,
    )
    fun countContestsByVacancyId(
        @Param("vacancyId") vacancyId: UUID,
    ): Long

    fun findAllByContestsId(contestId: UUID): List<VacancyEntity>

    fun countByTeamIdAndStatusAndCreatedAtLessThanEqual(
        teamId: UUID,
        status: VacancyStatus,
        to: Instant,
    ): Long

    fun countByTeamCompanyIdAndStatusAndCreatedAtLessThanEqual(
        companyId: UUID,
        status: VacancyStatus,
        to: Instant,
    ): Long

    fun countByTeamIdAndClosedAtGreaterThanEqualAndClosedAtLessThan(
        teamId: UUID,
        from: Instant,
        to: Instant,
    ): Long

    fun countByTeamCompanyIdAndClosedAtGreaterThanEqualAndClosedAtLessThan(
        companyId: UUID,
        from: Instant,
        to: Instant,
    ): Long

    @Query(
        """
        SELECT new ru.ifmo.se.api.infrastructure.postgres.projection.VacancySummaryProjection(
            v.id, v.title, v.city, v.salary, v.status, v.createdAt,
            v.team.company.id, v.team.company.name
        )
        FROM VacancyEntity v
        """
    )
    fun findAllSummaries(
        spec: Specification<VacancyEntity>,
        pageable: Pageable,
    ): Page<VacancySummaryProjection>
}
