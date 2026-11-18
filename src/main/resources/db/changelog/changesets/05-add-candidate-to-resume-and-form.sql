--liquibase formatted sql
--changeset itmo-team:05-add-candidate-to-resume-and-form

ALTER TABLE resume
    ADD COLUMN candidate_id UUID NOT NULL REFERENCES person(person_id) ON DELETE CASCADE;

ALTER TABLE form
    ADD COLUMN candidate_id UUID NOT NULL REFERENCES person(person_id) ON DELETE CASCADE;
