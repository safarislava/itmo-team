--liquibase formatted sql
--changeset itmo-team:14-coder-report-date

ALTER TABLE coder_report
    ALTER COLUMN report_date DROP DEFAULT,
    ALTER COLUMN report_date TYPE DATE USING (report_date AT TIME ZONE 'UTC')::DATE,
    ALTER COLUMN report_date SET DEFAULT CURRENT_DATE;
