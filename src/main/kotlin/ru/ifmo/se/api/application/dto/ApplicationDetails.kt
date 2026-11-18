package ru.ifmo.se.api.application.dto

import ru.ifmo.se.api.domain.model.Application
import ru.ifmo.se.api.domain.model.Form
import ru.ifmo.se.api.domain.model.Resume

data class ApplicationDetails(
    val application: Application,
    val resume: Resume,
    val form: Form,
)
