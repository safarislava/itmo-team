package ru.ifmo.se.api.domain.model

data class PageQuery(
    val pageNumber: Int,
    val pageSize: Int,
    val sortBy: String = "id",
    val sortOrder: SortDirection,
)
