--liquibase formatted sql
--changeset itmo-team:03-create-indexes

CREATE INDEX idx_vacancy_city
    ON vacancy (city);

CREATE INDEX idx_vacancy_skills_open
    ON vacancy USING gin (skill_ids)
    WHERE status = 'OPEN';

CREATE INDEX idx_vacancy_team_id
    ON vacancy (team_id);

CREATE INDEX idx_form_skill_skill_id
    ON form_skill (skill_id, form_id);

CREATE INDEX idx_application_vacancy_id
    ON application (vacancy_id);

CREATE INDEX idx_interview_person_id
    ON interview (candidate_id);

CREATE INDEX idx_vacancy_contest_vacancy_id
    ON vacancy_contest (vacancy_id);

CREATE INDEX idx_contest_task_contest_id
    ON contest_task (contest_id);

CREATE INDEX idx_test_task_id
    ON test (task_id);

CREATE INDEX idx_submission_task_id
    ON submission (task_id);
