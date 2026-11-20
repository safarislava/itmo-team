package ru.ifmo.se.api.presentation.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.application.dto.TestDetails
import ru.ifmo.se.api.presentation.dto.response.TestResponse

@Component
class TestResponseMapper {
    fun toResponse(details: TestDetails): TestResponse =
        TestResponse(
            id = details.id.value,
            taskId = details.taskId.value,
            input = details.input,
            output = details.output,
            hidden = details.hidden,
        )
}
