--liquibase formatted sql
--changeset itmo-team:10-language-commands

ALTER TABLE language
    ADD COLUMN source_filename VARCHAR(100) NOT NULL DEFAULT 'solution.cpp',
    ADD COLUMN compile_command VARCHAR(500) NULL,
    ADD COLUMN run_command VARCHAR(500) NOT NULL DEFAULT './solution';
