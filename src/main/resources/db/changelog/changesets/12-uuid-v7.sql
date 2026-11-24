--liquibase formatted sql
--changeset itmo-team:12-uuid-v7 splitStatements:false

CREATE OR REPLACE FUNCTION uuid_generate_v7()
RETURNS uuid
LANGUAGE plpgsql
VOLATILE
AS $$
DECLARE
    v_bytes bytea;
BEGIN
    v_bytes := uuid_send(gen_random_uuid());
    v_bytes := overlay(
        v_bytes
        PLACING substring(int8send(floor(extract(epoch FROM clock_timestamp()) * 1000)::bigint) FROM 3)
        FROM 1 FOR 6
    );
    v_bytes := set_byte(v_bytes, 6, (get_byte(v_bytes, 6) & 15) | 112);
    RETURN encode(v_bytes, 'hex')::uuid;
END;
$$;

CREATE OR REPLACE FUNCTION uuidv7()
RETURNS uuid
LANGUAGE sql
AS $$
    SELECT uuid_generate_v7();
$$;

ALTER TABLE company ALTER COLUMN company_id SET DEFAULT uuid_generate_v7();
ALTER TABLE person ALTER COLUMN person_id SET DEFAULT uuid_generate_v7();
ALTER TABLE team ALTER COLUMN team_id SET DEFAULT uuid_generate_v7();
ALTER TABLE coder_report ALTER COLUMN report_id SET DEFAULT uuid_generate_v7();
ALTER TABLE vacancy ALTER COLUMN vacancy_id SET DEFAULT uuid_generate_v7();
ALTER TABLE skill ALTER COLUMN skill_id SET DEFAULT uuid_generate_v7();
ALTER TABLE resume ALTER COLUMN resume_id SET DEFAULT uuid_generate_v7();
ALTER TABLE form ALTER COLUMN form_id SET DEFAULT uuid_generate_v7();
ALTER TABLE application ALTER COLUMN application_id SET DEFAULT uuid_generate_v7();
ALTER TABLE application_response ALTER COLUMN response_id SET DEFAULT uuid_generate_v7();
ALTER TABLE interview ALTER COLUMN interview_id SET DEFAULT uuid_generate_v7();
ALTER TABLE interview_feedback ALTER COLUMN feedback_id SET DEFAULT uuid_generate_v7();
ALTER TABLE contest ALTER COLUMN contest_id SET DEFAULT uuid_generate_v7();
ALTER TABLE task ALTER COLUMN task_id SET DEFAULT uuid_generate_v7();
ALTER TABLE test ALTER COLUMN test_id SET DEFAULT uuid_generate_v7();
ALTER TABLE language ALTER COLUMN language_id SET DEFAULT uuid_generate_v7();
ALTER TABLE submission ALTER COLUMN submission_id SET DEFAULT uuid_generate_v7();
ALTER TABLE runner_server ALTER COLUMN runner_server_id SET DEFAULT uuid_generate_v7();
ALTER TABLE refresh_token ALTER COLUMN token_id SET DEFAULT uuid_generate_v7();
