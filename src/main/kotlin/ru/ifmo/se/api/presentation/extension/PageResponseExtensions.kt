package ru.ifmo.se.api.presentation.extension

import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.presentation.dto.response.PageResponse

inline fun <T, R> PageResult<T>.toPageResponse(
    size: Int,
    transform: (T) -> R,
): PageResponse<R> =
    PageResponse(
        page = pageNumber,
        size = size,
        totalElements = totalElements,
        totalPages = totalPages,
        content = items.map(transform),
    )

fun <T> PageResult<T>.toPageResponse(size: Int): PageResponse<T> =
    PageResponse(
        page = pageNumber,
        size = size,
        totalElements = totalElements,
        totalPages = totalPages,
        content = items,
    )
