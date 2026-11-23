package ru.ifmo.se.api.presentation.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.ifmo.se.api.application.dto.CreateTaskCommand
import ru.ifmo.se.api.application.dto.UpdateTaskCommand
import ru.ifmo.se.api.application.service.TaskService
import ru.ifmo.se.api.domain.exception.TaskBadRequestException
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.SortDirection
import ru.ifmo.se.api.domain.model.TaskFilter
import ru.ifmo.se.api.domain.model.TaskId
import ru.ifmo.se.api.infrastructure.security.annotation.RequiresRole
import ru.ifmo.se.api.presentation.dto.request.TaskRequest
import ru.ifmo.se.api.presentation.dto.response.PageResponse
import ru.ifmo.se.api.presentation.dto.response.TaskResponse
import ru.ifmo.se.api.presentation.extension.toPageResponse
import ru.ifmo.se.api.presentation.extension.toSortDirectionOrNull
import ru.ifmo.se.api.presentation.mapper.TaskResponseMapper
import java.util.UUID

@Validated
@RestController
@RequestMapping("/api/tasks")
class TaskController(
    private val taskService: TaskService,
    private val taskResponseMapper: TaskResponseMapper,
) {
    @GetMapping
    @RequiresRole(PersonRole.TEAM_LEAD, PersonRole.ADMIN)
    fun listTasks(
        @AuthenticationPrincipal currentPerson: Person,
        @Min(0) @RequestParam(defaultValue = "0") page: Int,
        @Min(1) @Max(100) @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") order: String,
        @RequestParam(required = false) search: String?,
        @RequestParam(required = false) authorId: UUID?,
    ): ResponseEntity<PageResponse<TaskResponse>> {
        val allowedSortColumns = setOf("id", "name", "timeLimit", "memoryLimit")
        if (sort !in allowedSortColumns) {
            throw TaskBadRequestException("Invalid sort column: $sort. Allowed values: id, name, timeLimit, memoryLimit.")
        }

        val sortDirection =
            order.toSortDirectionOrNull()
                ?: throw TaskBadRequestException("Invalid order: $order. Allowed values: asc, desc.")

        val pageQuery =
            PageQuery(
                pageNumber = page,
                pageSize = size,
                sortBy = sort,
                sortOrder = sortDirection,
            )

        val filter =
            TaskFilter(
                search = search,
                authorId = authorId?.let { PersonId(it) },
            )

        val pageResult = taskService.getTasks(currentPerson, pageQuery, filter)
        return ResponseEntity.ok(pageResult.toPageResponse(size, taskResponseMapper::toResponse))
    }

    @PostMapping
    @RequiresRole(PersonRole.TEAM_LEAD, PersonRole.ADMIN)
    fun createTask(
        @AuthenticationPrincipal currentPerson: Person,
        @Valid @RequestBody request: TaskRequest,
    ): ResponseEntity<TaskResponse> {
        val command =
            CreateTaskCommand(
                name = request.name,
                description = request.description,
                timeLimit = request.timeLimit,
                memoryLimit = request.memoryLimit,
            )
        val details = taskService.createTask(currentPerson, command)
        return ResponseEntity.status(HttpStatus.CREATED).body(taskResponseMapper.toResponse(details))
    }

    @GetMapping("/{taskId}")
    @RequiresRole(
        PersonRole.CANDIDATE,
        PersonRole.TEAM_LEAD,
        PersonRole.ADMIN,
    )
    fun getTask(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable taskId: UUID,
    ): ResponseEntity<TaskResponse> {
        val details = taskService.getTask(currentPerson, TaskId(taskId))
        return ResponseEntity.ok(taskResponseMapper.toResponse(details))
    }

    @PutMapping("/{taskId}")
    @RequiresRole(PersonRole.TEAM_LEAD, PersonRole.ADMIN)
    fun updateTask(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable taskId: UUID,
        @Valid @RequestBody request: TaskRequest,
    ): ResponseEntity<TaskResponse> {
        val command =
            UpdateTaskCommand(
                name = request.name,
                description = request.description,
                timeLimit = request.timeLimit,
                memoryLimit = request.memoryLimit,
            )
        val details = taskService.updateTask(currentPerson, TaskId(taskId), command)
        return ResponseEntity.ok(taskResponseMapper.toResponse(details))
    }

    @DeleteMapping("/{taskId}")
    @RequiresRole(PersonRole.TEAM_LEAD, PersonRole.ADMIN)
    fun deleteTask(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable taskId: UUID,
    ): ResponseEntity<Void> {
        taskService.deleteTask(currentPerson, TaskId(taskId))
        return ResponseEntity.noContent().build()
    }
}
