package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "team")
class TeamEntity(
    @Id
    @Column(name = "team_id")
    override var id: UUID,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teamlead_id")
    var teamlead: PersonEntity,
    var name: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    var company: CompanyEntity,
) : BasicEntity()
