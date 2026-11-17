package ru.ifmo.se.api.presentation.dto.response

data class PageResponse<T>(
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val content: List<T>,
)
