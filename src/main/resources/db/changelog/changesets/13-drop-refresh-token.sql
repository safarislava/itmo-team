--liquibase formatted sql
--changeset itmo-team:13-drop-refresh-token

DROP TABLE IF EXISTS refresh_token;
