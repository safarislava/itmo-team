package ru.ifmo.se.api.presentation.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.ifmo.se.api.application.dto.CreateTestCommand
import ru.ifmo.se.api.application.dto.ReplaceTestsCommand
import ru.ifmo.se.api.application.dto.UpdateTestCommand
import ru.ifmo.se.api.application.service.TestService
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.TaskId
import ru.ifmo.se.api.domain.model.TestId
import ru.ifmo.se.api.infrastructure.security.annotation.RequiresRole
import ru.ifmo.se.api.presentation.dto.request.TestRequest
import ru.ifmo.se.api.presentation.dto.response.TestResponse
import ru.ifmo.se.api.presentation.mapper.TestResponseMapper
import java.util.UUID

@Validated
@RestController
@RequestMapping("/api/tasks")
class TestController(
    private val testService: TestService,
    private val testResponseMapper: TestResponseMapper,
) {
    @GetMapping("/{taskId}/tests")
    @RequiresRole(
        PersonRole.CANDIDATE,
        PersonRole.TEAM_LEAD,
        PersonRole.ADMIN,
    )
    fun listTests(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable taskId: UUID,
    ): ResponseEntity<List<TestResponse>> {
        val tests = testService.listTests(currentPerson, TaskId(taskId))
        return ResponseEntity.ok(tests.map(testResponseMapper::toResponse))
    }

    @PostMapping("/{taskId}/tests")
    @RequiresRole(PersonRole.TEAM_LEAD, PersonRole.ADMIN)
    fun createTest(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable taskId: UUID,
        @Valid @RequestBody request: TestRequest,
    ): ResponseEntity<TestResponse> {
        val command =
            CreateTestCommand(
                input = request.input,
                output = request.output,
                hidden = request.hidden,
            )
        val created = testService.createTest(currentPerson, TaskId(taskId), command)
        return ResponseEntity.status(HttpStatus.CREATED).body(testResponseMapper.toResponse(created))
    }

    @PutMapping(
        "/{taskId}/tests",
        consumes = ["application/zip", MediaType.APPLICATION_OCTET_STREAM_VALUE],
    )
    @RequiresRole(PersonRole.TEAM_LEAD, PersonRole.ADMIN)
    fun replaceTests(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable taskId: UUID,
        @RequestBody zipBytes: ByteArray,
    ): ResponseEntity<List<TestResponse>> {
        val command = ReplaceTestsCommand(bytes = zipBytes)
        val tests = testService.replaceTests(currentPerson, TaskId(taskId), command)
        return ResponseEntity.ok(tests.map(testResponseMapper::toResponse))
    }

    @PutMapping("/{taskId}/tests/{testId}")
    @RequiresRole(PersonRole.TEAM_LEAD, PersonRole.ADMIN)
    fun updateTest(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable taskId: UUID,
        @PathVariable testId: UUID,
        @Valid @RequestBody request: TestRequest,
    ): ResponseEntity<TestResponse> {
        val command =
            UpdateTestCommand(
                input = request.input,
                output = request.output,
                hidden = request.hidden,
            )
        val updated = testService.updateTest(currentPerson, TaskId(taskId), TestId(testId), command)
        return ResponseEntity.ok(testResponseMapper.toResponse(updated))
    }

    @DeleteMapping("/{taskId}/tests/{testId}")
    @RequiresRole(PersonRole.TEAM_LEAD, PersonRole.ADMIN)
    fun deleteTest(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable taskId: UUID,
        @PathVariable testId: UUID,
    ): ResponseEntity<Void> {
        testService.deleteTest(currentPerson, TaskId(taskId), TestId(testId))
        return ResponseEntity.noContent().build()
    }
}
