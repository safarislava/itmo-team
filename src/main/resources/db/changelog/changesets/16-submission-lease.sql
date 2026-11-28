--liquibase formatted sql
--changeset itmo-team:16-submission-lease

ALTER TABLE submission
    ADD COLUMN lease_token UUID,
    ADD COLUMN lease_until TIMESTAMPTZ,
    ADD COLUMN retry_count SMALLINT NOT NULL DEFAULT 0 CHECK (retry_count >= 0);

CREATE INDEX idx_submission_running_lease_until
    ON submission (lease_until)
    WHERE status = 'RUNNING';
