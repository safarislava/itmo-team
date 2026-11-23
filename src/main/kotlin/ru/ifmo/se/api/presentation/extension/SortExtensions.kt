package ru.ifmo.se.api.presentation.extension

import ru.ifmo.se.api.domain.model.SortDirection

fun String.toSortDirectionOrNull(): SortDirection? =
    when (trim().lowercase()) {
        "asc" -> SortDirection.ASC
        "desc" -> SortDirection.DESC
        else -> null
    }
