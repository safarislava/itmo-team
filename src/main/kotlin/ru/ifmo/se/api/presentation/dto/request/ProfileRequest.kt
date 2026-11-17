package ru.ifmo.se.api.presentation.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ProfileRequest(
    @field:NotBlank
    @field:Size(min = 1, max = 100)
    val name: String,
    @field:NotBlank
    @field:Email
    val email: String,
    val githubId: Long? = null,
)
