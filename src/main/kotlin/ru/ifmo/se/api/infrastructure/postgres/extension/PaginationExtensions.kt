package ru.ifmo.se.api.infrastructure.postgres.extension

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.SortDirection

fun SortDirection.toSpringDirection(): Sort.Direction = if (this == SortDirection.DESC) Sort.Direction.DESC else Sort.Direction.ASC

fun PageQuery.toPageRequest(sortPropertyResolver: (String) -> String = { it }): PageRequest {
    val direction = sortOrder.toSpringDirection()
    val property = sortPropertyResolver(sortBy)
    return PageRequest.of(pageNumber, pageSize, Sort.by(direction, property))
}

fun PageQuery.toPageRequest(sortProperty: String): PageRequest =
    PageRequest.of(pageNumber, pageSize, Sort.by(sortOrder.toSpringDirection(), sortProperty))

fun <E : Any, D> Page<E>.toPageResult(transform: (E) -> D): PageResult<D> =
    PageResult(
        items = content.map(transform),
        totalElements = totalElements,
        pageNumber = number,
        totalPages = totalPages,
    )
