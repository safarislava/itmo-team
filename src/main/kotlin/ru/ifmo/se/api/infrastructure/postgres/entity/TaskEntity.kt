package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "task")
class TaskEntity(
    @Id
    @Column(name = "task_id")
    override var id: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    var author: PersonEntity,

    var name: String,
    var description: String?,
    var timeLimit: Int,
    var memoryLimit: Int
) : BasicEntity()
