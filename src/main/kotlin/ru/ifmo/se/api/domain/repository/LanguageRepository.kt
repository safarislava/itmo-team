package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.Language
import ru.ifmo.se.api.domain.model.LanguageId

interface LanguageRepository {
    fun findById(id: LanguageId): Language?

    fun findAllById(ids: Iterable<LanguageId>): List<Language>

    fun findAll(): List<Language>

    fun save(language: Language): Language

    fun delete(id: LanguageId)
}
