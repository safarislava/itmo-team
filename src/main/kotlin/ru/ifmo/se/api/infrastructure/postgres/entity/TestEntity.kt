package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.*
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
    var isHidden: Boolean
) : BasicEntity()
