package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.RunnerServer
import ru.ifmo.se.api.domain.model.RunnerServerId

interface RunnerServerRepository {
    fun findById(id: RunnerServerId): RunnerServer?

    fun findAll(): List<RunnerServer>
}
