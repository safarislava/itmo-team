package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "application")
class ApplicationEntity(
    @Id
    @Column(name = "application_id")
    override var id: UUID,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id")
    var candidate: PersonEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vacancy_id")
    var vacancy: VacancyEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    var resume: ResumeEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id")
    var form: FormEntity,
    @Enumerated(EnumType.STRING)
    var status: ApplicationStatus,
) : BasicEntity()
