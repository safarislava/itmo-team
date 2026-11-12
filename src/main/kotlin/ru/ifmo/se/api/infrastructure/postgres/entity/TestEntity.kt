package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "test")
class TestEntity(
    @Id
    @Column(name = "test_id")
    override var id: UUID,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    var task: TaskEntity,
    var inputData: String,
    var outputData: String,
    var isHidden: Boolean,
) : BasicEntity()
