package ru.ifmo.se.api.presentation.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.Contest
import ru.ifmo.se.api.presentation.dto.response.ContestResponse

@Component
class ContestResponseMapper {
    fun toResponse(contest: Contest): ContestResponse =
        ContestResponse(
            id = contest.id.value,
            name = contest.name,
            scheduledTime = contest.scheduledTime,
            duration = contest.duration,
            endTime = contest.endTime,
        )
}
