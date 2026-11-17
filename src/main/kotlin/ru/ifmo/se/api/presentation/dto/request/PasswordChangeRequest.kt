package ru.ifmo.se.api.presentation.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class PasswordChangeRequest(
    @field:NotBlank
    val currentPassword: String,
    @field:NotBlank
    @field:Size(min = 8, max = 72)
    val newPassword: String,
)
