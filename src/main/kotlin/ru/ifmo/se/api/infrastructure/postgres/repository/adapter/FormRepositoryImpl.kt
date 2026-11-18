package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import org.springframework.stereotype.Repository
import ru.ifmo.se.api.domain.model.Form
import ru.ifmo.se.api.domain.model.FormId
import ru.ifmo.se.api.domain.repository.FormRepository
import ru.ifmo.se.api.infrastructure.postgres.mapper.FormEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaFormRepository

@Repository
class FormRepositoryImpl(
    private val repo: JpaFormRepository,
    private val mapper: FormEntityMapper,
) : FormRepository {
    override fun findById(id: FormId): Form? = repo.findById(id.value).map(mapper::toDomain).orElse(null)

    override fun save(form: Form): Form = mapper.toDomain(repo.save(mapper.toEntity(form)))

    override fun delete(form: Form) {
        repo.deleteById(form.id.value)
    }
}
