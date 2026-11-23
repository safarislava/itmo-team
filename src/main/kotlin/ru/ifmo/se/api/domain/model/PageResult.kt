package ru.ifmo.se.api.domain.model

data class PageResult<T>(
    val items: List<T>,
    val totalElements: Long,
    val pageNumber: Int,
    val totalPages: Int,
) {
    companion object {
        fun <T> empty(pageNumber: Int = 0): PageResult<T> =
            PageResult(
                items = emptyList(),
                totalElements = 0,
                pageNumber = pageNumber,
                totalPages = 0,
            )
    }
}

inline fun <T, R> PageResult<T>.map(transform: (T) -> R): PageResult<R> =
    PageResult(
        items = items.map(transform),
        totalElements = totalElements,
        pageNumber = pageNumber,
        totalPages = totalPages,
    )
