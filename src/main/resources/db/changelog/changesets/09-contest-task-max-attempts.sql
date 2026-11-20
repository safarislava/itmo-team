--liquibase formatted sql
--changeset itmo-team:09-contest-task-max-attempts

ALTER TABLE contest_task
    ADD COLUMN max_attempts SMALLINT NOT NULL DEFAULT 99 CONSTRAINT chk_contest_task_max_attempts CHECK (max_attempts >= 1);
