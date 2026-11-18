package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.Form
import ru.ifmo.se.api.domain.model.FormId

interface FormRepository {
    fun findById(id: FormId): Form?

    fun save(form: Form): Form

    fun delete(form: Form)
}
