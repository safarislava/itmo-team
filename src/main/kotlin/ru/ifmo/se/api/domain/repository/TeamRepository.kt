package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.Team
import ru.ifmo.se.api.domain.model.TeamId

interface TeamRepository {
    fun findById(id: TeamId): Team?
}
