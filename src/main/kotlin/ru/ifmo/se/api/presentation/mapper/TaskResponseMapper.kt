package ru.ifmo.se.api.presentation.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.application.dto.TaskDetails
import ru.ifmo.se.api.presentation.dto.response.TaskResponse

@Component
class TaskResponseMapper(
    private val personResponseMapper: PersonResponseMapper,
) {
    fun toResponse(details: TaskDetails): TaskResponse =
        TaskResponse(
            id = details.task.id.value,
            name = details.task.name,
            description = details.task.description,
            timeLimit = details.task.timeLimit,
            memoryLimit = details.task.memoryLimit,
            author = personResponseMapper.toSummaryResponse(details.author),
        )
}
