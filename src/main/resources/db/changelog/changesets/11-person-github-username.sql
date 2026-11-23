--liquibase formatted sql
--changeset itmo-team:11-person-github-username

ALTER TABLE person
    DROP COLUMN github_id;

ALTER TABLE person
    ADD COLUMN github_username VARCHAR(100) UNIQUE;
