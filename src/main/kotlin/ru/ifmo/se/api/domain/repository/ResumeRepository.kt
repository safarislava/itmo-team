package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.Resume
import ru.ifmo.se.api.domain.model.ResumeId

interface ResumeRepository {
    fun findById(id: ResumeId): Resume?

    fun save(resume: Resume): Resume

    fun delete(resume: Resume)
}
