package ru.ifmo.se.api.domain.model

data class PersonFilter(
    val search: String? = null,
    val role: PersonRole? = null,
    val companyId: CompanyId? = null,
    val teamId: TeamId? = null,
)

data class PageQuery(
    val pageNumber: Int,
    val pageSize: Int,
    val sortBy: String = "id",
    val sortOrder: SortDirection,
)

enum class SortDirection {
    ASC,
    DESC,
}

data class PageResult<T>(
    val items: List<T>,
    val totalElements: Long,
    val pageNumber: Int,
    val totalPages: Int,
)
