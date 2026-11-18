package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import org.springframework.stereotype.Repository
import ru.ifmo.se.api.domain.model.Resume
import ru.ifmo.se.api.domain.model.ResumeId
import ru.ifmo.se.api.domain.repository.ResumeRepository
import ru.ifmo.se.api.infrastructure.postgres.mapper.ResumeEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaResumeRepository

@Repository
class ResumeRepositoryImpl(
    private val repo: JpaResumeRepository,
    private val mapper: ResumeEntityMapper,
) : ResumeRepository {
    override fun findById(id: ResumeId): Resume? = repo.findById(id.value).map(mapper::toDomain).orElse(null)

    override fun save(resume: Resume): Resume = mapper.toDomain(repo.save(mapper.toEntity(resume)))

    override fun delete(resume: Resume) {
        repo.deleteById(resume.id.value)
    }
}
