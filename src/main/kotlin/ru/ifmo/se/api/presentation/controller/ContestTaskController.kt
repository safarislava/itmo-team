package ru.ifmo.se.api.presentation.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.ifmo.se.api.application.service.ContestTaskService
import ru.ifmo.se.api.domain.model.ContestId
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.TaskId
import ru.ifmo.se.api.infrastructure.security.annotation.RequiresRole
import ru.ifmo.se.api.presentation.dto.request.ContestTaskRequest
import ru.ifmo.se.api.presentation.dto.response.TaskResponse
import ru.ifmo.se.api.presentation.mapper.TaskResponseMapper
import java.util.UUID

@Validated
@RestController
@RequestMapping("/api/contests/{contestId}/tasks")
class ContestTaskController(
    private val contestTaskService: ContestTaskService,
    private val taskResponseMapper: TaskResponseMapper,
) {
    @GetMapping
    @RequiresRole(
        PersonRole.CANDIDATE,
        PersonRole.HR,
        PersonRole.TEAM_LEAD,
        PersonRole.ADMIN,
    )
    fun listContestTasks(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable contestId: UUID,
    ): ResponseEntity<List<TaskResponse>> {
        val tasks = contestTaskService.listContestTasks(currentPerson, ContestId(contestId))
        return ResponseEntity.ok(tasks.map(taskResponseMapper::toResponse))
    }

    @PutMapping("/{taskId}")
    @RequiresRole(PersonRole.TEAM_LEAD, PersonRole.ADMIN)
    fun addTaskToContest(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable contestId: UUID,
        @PathVariable taskId: UUID,
        @Valid @RequestBody request: ContestTaskRequest,
    ): ResponseEntity<Void> {
        contestTaskService.addTaskToContest(currentPerson, ContestId(contestId), TaskId(taskId), request.maxAttempts)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{taskId}")
    @RequiresRole(PersonRole.TEAM_LEAD, PersonRole.ADMIN)
    fun removeTaskFromContest(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable contestId: UUID,
        @PathVariable taskId: UUID,
    ): ResponseEntity<Void> {
        contestTaskService.removeTaskFromContest(currentPerson, ContestId(contestId), TaskId(taskId))
        return ResponseEntity.noContent().build()
    }
}
