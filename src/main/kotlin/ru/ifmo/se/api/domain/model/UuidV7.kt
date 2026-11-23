package ru.ifmo.se.api.domain.model

import com.github.f4b6a3.uuid.UuidCreator
import java.util.UUID

fun uuidV7(): UUID = UuidCreator.getTimeOrderedEpoch()
