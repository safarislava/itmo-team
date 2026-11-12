package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.UUID

@Entity
@Table(name = "runner_server")
class RunnerServerEntity(
    @Id
    @Column(name = "runner_server_id")
    override var id: UUID,
    @JdbcTypeCode(SqlTypes.INET)
    @Column(name = "ip", columnDefinition = "inet")
    var ip: String,
) : BasicEntity()
