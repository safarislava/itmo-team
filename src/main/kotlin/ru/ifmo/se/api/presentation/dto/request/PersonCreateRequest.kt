package ru.ifmo.se.api.presentation.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import ru.ifmo.se.api.domain.model.PersonRole
import java.util.UUID

data class PersonCreateRequest(
    @field:NotBlank
    @field:Size(min = 1, max = 100)
    val name: String,
    @field:NotBlank
    @field:Email
    @field:Size(max = 255)
    val email: String,
    val role: PersonRole,
    val companyId: UUID? = null,
    val teamId: UUID? = null,
)
