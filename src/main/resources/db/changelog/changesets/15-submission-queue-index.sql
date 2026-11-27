--liquibase formatted sql
--changeset itmo-team:15-submission-queue-index

CREATE INDEX idx_submission_queued_submitted_at
    ON submission (submitted_at)
    WHERE status = 'QUEUED';
