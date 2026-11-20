--liquibase formatted sql
--changeset itmo-team:07-tests-s3

TRUNCATE TABLE test;

ALTER TABLE test
    DROP COLUMN IF EXISTS input_data,
    DROP COLUMN IF EXISTS output_data,
    ADD COLUMN input_s3key VARCHAR(1024) NOT NULL,
    ADD COLUMN output_s3key VARCHAR(1024) NOT NULL;
