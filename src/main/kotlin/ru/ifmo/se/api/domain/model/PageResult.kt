package ru.ifmo.se.api.domain.model

data class PageResult<T>(
    val items: List<T>,
    val totalElements: Long,
    val pageNumber: Int,
    val totalPages: Int,
)
