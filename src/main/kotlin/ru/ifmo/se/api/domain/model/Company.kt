package ru.ifmo.se.api.domain.model

import java.time.LocalDate

data class Company(
    val id: CompanyId,
    val name: String,
    val creationDate: LocalDate,
)
