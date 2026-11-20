--liquibase formatted sql
--changeset itmo-team:08-contest-name

ALTER TABLE contest
    ADD COLUMN name VARCHAR(255) NOT NULL DEFAULT 'Contest' CHECK (btrim(name) <> '');

ALTER TABLE contest
    ALTER COLUMN name DROP DEFAULT;
