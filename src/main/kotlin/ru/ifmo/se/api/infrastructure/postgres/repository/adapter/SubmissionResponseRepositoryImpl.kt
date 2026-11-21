package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import org.springframework.stereotype.Repository
import ru.ifmo.se.api.domain.model.SubmissionId
import ru.ifmo.se.api.domain.model.SubmissionResponse
import ru.ifmo.se.api.domain.repository.SubmissionResponseRepository
import ru.ifmo.se.api.infrastructure.postgres.mapper.SubmissionResponseEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaSubmissionResponseRepository

@Repository
class SubmissionResponseRepositoryImpl(
    private val repo: JpaSubmissionResponseRepository,
    private val mapper: SubmissionResponseEntityMapper,
) : SubmissionResponseRepository {
    override fun findById(submissionId: SubmissionId): SubmissionResponse? =
        repo.findById(submissionId.value).map(mapper::toDomain).orElse(null)

    override fun save(response: SubmissionResponse): SubmissionResponse = mapper.toDomain(repo.save(mapper.toEntity(response)))

    override fun delete(submissionId: SubmissionId) = repo.deleteById(submissionId.value)
}
