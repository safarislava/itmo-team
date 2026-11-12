--liquibase formatted sql
--changeset itmo-team:01-create-schema

CREATE TABLE company (
    company_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL UNIQUE CHECK (btrim(name) <> ''),
    creation_date DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE person (
    person_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL CHECK (btrim(name) <> ''),
    email VARCHAR(255) NOT NULL UNIQUE CHECK (email LIKE '%@%' AND btrim(email) <> ''),
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('CANDIDATE', 'HR', 'TEAM_LEAD', 'DEVELOPER', 'MANAGER', 'ADMIN')),
    company_id UUID REFERENCES company(company_id) ON DELETE SET NULL,
    team_id UUID,
    github_id BIGINT UNIQUE
);

CREATE TABLE team (
    team_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    teamlead_id UUID NOT NULL UNIQUE REFERENCES person(person_id) ON DELETE RESTRICT,
    name VARCHAR(100) NOT NULL CHECK (btrim(name) <> ''),
    company_id UUID NOT NULL REFERENCES company(company_id) ON DELETE CASCADE,
    CONSTRAINT uq_team_company_name UNIQUE (company_id, name)
);

ALTER TABLE person
    ADD CONSTRAINT fk_person_team
    FOREIGN KEY (team_id) REFERENCES team(team_id) ON DELETE SET NULL;

CREATE TABLE coder_report (
    report_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    coder_id UUID REFERENCES person(person_id) ON DELETE CASCADE,
    count_opened_pr INTEGER NOT NULL DEFAULT 0 CHECK (count_opened_pr >= 0),
    count_closed_pr INTEGER NOT NULL DEFAULT 0 CHECK (count_closed_pr >= 0),
    count_reviewed_pr INTEGER NOT NULL DEFAULT 0 CHECK (count_reviewed_pr >= 0),
    count_commits INTEGER NOT NULL DEFAULT 0 CHECK (count_commits >= 0),
    report_date TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE vacancy (
    vacancy_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL CHECK (btrim(title) <> ''),
    description TEXT NOT NULL CHECK (btrim(description) <> ''),
    team_id UUID NOT NULL REFERENCES team(team_id) ON DELETE CASCADE,
    city VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('DRAFT', 'OPEN', 'CLOSED', 'ARCHIVE')),
    salary INTEGER CHECK (salary >= 0),
    skill_ids UUID[],
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    closed_at TIMESTAMPTZ CHECK (closed_at > created_at)
);

CREATE TABLE skill (
    skill_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL CHECK (btrim(name) <> ''),
    description TEXT CHECK (btrim(description) <> '')
);

CREATE TABLE resume (
    resume_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    s3key VARCHAR(1024) NOT NULL UNIQUE,
    filename VARCHAR(255) NOT NULL
);

CREATE TABLE form (
    form_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    university VARCHAR(255) CHECK (btrim(university) <> ''),
    motivation TEXT CHECK (btrim(motivation) <> ''),
    pet_projects TEXT CHECK (btrim(pet_projects) <> ''),
    job_exp TEXT CHECK (btrim(job_exp) <> ''),
    work_format VARCHAR(20) CHECK (work_format IN ('OFFICE', 'DISTANT', 'HYBRID')),
    skill_ids UUID[]
);

CREATE TABLE application (
    application_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id UUID NOT NULL REFERENCES person(person_id) ON DELETE CASCADE,
    vacancy_id UUID NOT NULL REFERENCES vacancy(vacancy_id) ON DELETE CASCADE,
    resume_id UUID NOT NULL REFERENCES resume(resume_id) ON DELETE RESTRICT,
    form_id UUID NOT NULL REFERENCES form(form_id) ON DELETE RESTRICT,
    status VARCHAR(20) NOT NULL DEFAULT 'NEW' CHECK (status IN ('NEW', 'REVIEW', 'CONTEST', 'INTERVIEW', 'OFFER', 'REJECTED')),
    CONSTRAINT uq_application_person_vacancy UNIQUE (candidate_id, vacancy_id)
);

CREATE TABLE application_response (
    response_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    application_id UUID NOT NULL REFERENCES application(application_id) ON DELETE CASCADE,
    hr_id UUID NOT NULL REFERENCES person(person_id) ON DELETE RESTRICT,
    motivation SMALLINT NOT NULL CHECK (motivation BETWEEN 0 AND 100),
    soft_skills SMALLINT NOT NULL CHECK (soft_skills BETWEEN 0 AND 100),
    pet_projects SMALLINT NOT NULL CHECK (pet_projects BETWEEN 0 AND 100),
    job_exp SMALLINT NOT NULL CHECK (job_exp BETWEEN 0 AND 100),
    hard_skills SMALLINT NOT NULL CHECK (hard_skills BETWEEN 0 AND 100),
    work_format SMALLINT NOT NULL CHECK (work_format BETWEEN 0 AND 100),
    CONSTRAINT uq_application_response_app_hr UNIQUE (application_id, hr_id)
);

CREATE TABLE application_score (
    application_id UUID PRIMARY KEY REFERENCES application(application_id) ON DELETE CASCADE,
    motivation SMALLINT NOT NULL CHECK (motivation BETWEEN 0 AND 100),
    soft_skills SMALLINT NOT NULL CHECK (soft_skills BETWEEN 0 AND 100),
    pet_projects SMALLINT NOT NULL CHECK (pet_projects BETWEEN 0 AND 100),
    job_exp SMALLINT NOT NULL CHECK (job_exp BETWEEN 0 AND 100),
    hard_skills SMALLINT NOT NULL CHECK (hard_skills BETWEEN 0 AND 100),
    work_format SMALLINT NOT NULL CHECK (work_format BETWEEN 0 AND 100)
);

CREATE TABLE interview (
    interview_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id UUID NOT NULL REFERENCES person(person_id) ON DELETE CASCADE,
    teamlead_id UUID NOT NULL REFERENCES person(person_id) ON DELETE RESTRICT,
    vacancy_id UUID NOT NULL REFERENCES vacancy(vacancy_id) ON DELETE CASCADE,
    video_s3key VARCHAR(1024) UNIQUE,
    scheduled_start TIMESTAMPTZ NOT NULL,
    scheduled_end TIMESTAMPTZ NOT NULL CHECK (scheduled_end > scheduled_start),
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED' CHECK (status IN ('SCHEDULED', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT chk_interview_person_teamlead CHECK (teamlead_id <> candidate_id)
);

CREATE TABLE interview_feedback (
    feedback_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    interview_id UUID NOT NULL UNIQUE REFERENCES interview(interview_id) ON DELETE CASCADE,
    review TEXT NOT NULL,
    score SMALLINT NOT NULL CHECK (score BETWEEN 1 AND 10),
    decision VARCHAR(20) NOT NULL CHECK (decision IN ('HIRE', 'NO_HIRE', 'NEXT_STAGE'))
);

CREATE TABLE contest (
    contest_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    scheduled_time TIMESTAMPTZ NOT NULL,
    duration INTERVAL NOT NULL CHECK (duration > INTERVAL '0')
);

CREATE TABLE task (
    task_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    author_id UUID NOT NULL REFERENCES person(person_id) ON DELETE RESTRICT,
    name VARCHAR(255) NOT NULL CHECK (btrim(name) <> ''),
    description TEXT,
    time_limit INTEGER NOT NULL CHECK (time_limit > 0),
    memory_limit INTEGER NOT NULL CHECK (memory_limit > 0)
);

CREATE TABLE test (
    test_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id UUID NOT NULL REFERENCES task(task_id) ON DELETE CASCADE,
    input_data TEXT NOT NULL,
    output_data TEXT NOT NULL,
    is_hidden BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE language (
    language_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL CHECK (btrim(name) <> ''),
    version VARCHAR(20) NOT NULL CHECK (btrim(version) <> ''),
    CONSTRAINT uq_language_name_version UNIQUE (name, version)
);

CREATE TABLE submission (
    submission_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sender_id UUID NOT NULL REFERENCES person(person_id) ON DELETE CASCADE,
    contest_id UUID NOT NULL REFERENCES contest(contest_id) ON DELETE CASCADE,
    task_id UUID NOT NULL REFERENCES task(task_id) ON DELETE CASCADE,
    language_id UUID NOT NULL REFERENCES language(language_id) ON DELETE RESTRICT,
    snapshot_s3key VARCHAR(1024) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'QUEUED' CHECK (status IN ('QUEUED', 'RUNNING', 'FINISHED', 'FAILED')),
    attempt SMALLINT NOT NULL DEFAULT 1 CHECK (attempt >= 1),
    ai_score SMALLINT CHECK (ai_score BETWEEN 0 AND 100),
    submitted_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_submission_attempt UNIQUE (sender_id, contest_id, task_id, attempt)
);

CREATE TABLE runner_server (
    runner_server_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ip INET NOT NULL UNIQUE
);

CREATE TABLE submission_response (
    submission_id UUID PRIMARY KEY REFERENCES submission(submission_id) ON DELETE CASCADE,
    verdict VARCHAR(30) NULL CHECK (verdict IN ('OK', 'WRONG_ANSWER', 'TIME_LIMIT', 'MEMORY_LIMIT', 'RUNTIME_ERROR', 'COMPILATION_ERROR')),
    execution_time INTEGER CHECK (execution_time >= 0),
    memory_used INTEGER CHECK (memory_used >= 0),
    runner_server_id UUID REFERENCES runner_server(runner_server_id) ON DELETE SET NULL
);

CREATE TABLE contest_task (
    contest_id UUID NOT NULL REFERENCES contest(contest_id) ON DELETE CASCADE,
    task_id UUID NOT NULL REFERENCES task(task_id) ON DELETE CASCADE,
    PRIMARY KEY (contest_id, task_id)
);

CREATE TABLE vacancy_contest (
    vacancy_id UUID NOT NULL REFERENCES vacancy(vacancy_id) ON DELETE CASCADE,
    contest_id UUID NOT NULL REFERENCES contest(contest_id) ON DELETE CASCADE,
    PRIMARY KEY (vacancy_id, contest_id)
);

CREATE TABLE contest_participant (
    contest_id UUID NOT NULL REFERENCES contest(contest_id) ON DELETE CASCADE,
    person_id UUID NOT NULL REFERENCES person(person_id) ON DELETE CASCADE,
    PRIMARY KEY (contest_id, person_id)
);
