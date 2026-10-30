--liquibase formatted sql
--changeset itmo-team:02-functions-and-triggers splitStatements:false

DO $$
BEGIN
    CREATE EXTENSION IF NOT EXISTS pg_cron;
EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'pg_cron extension not available, skipping...';
END $$;

CREATE OR REPLACE FUNCTION
    match_vacancies(
        p_skill_ids BIGINT[],
        p_city VARCHAR(255) DEFAULT NULL,
        p_min_salary INTEGER DEFAULT NULL,
        p_max_salary INTEGER DEFAULT NULL
    )
RETURNS TABLE (
    vacancy_id BIGINT,
    title VARCHAR,
    description TEXT,
    city VARCHAR,
    salary INTEGER
) AS $$
BEGIN
    RETURN QUERY
    SELECT v.vacancy_id, v.title, v.description, v.city, v.salary FROM vacancy v
    WHERE v.status = 'OPEN'
        AND (COALESCE(p_skill_ids, '{}') <@ (
            SELECT ARRAY_AGG(vs.skill_id)
            FROM vacancy_skill vs
            WHERE vs.vacancy_id = v.vacancy_id
        ))
        AND (p_min_salary IS NULL OR v.salary >= p_min_salary)
        AND (p_max_salary IS NULL OR v.salary <= p_max_salary)
        AND (p_city IS NULL OR v.city = p_city);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION
    close_vacancy_with_rejections(
       p_vacancy_id BIGINT
    )
RETURNS TABLE (
       rejected_application_id BIGINT
    )
AS $$
BEGIN
    UPDATE vacancy
    SET status = 'CLOSED',
        closed_at = NOW()
    WHERE vacancy_id = p_vacancy_id;

    RETURN QUERY
    UPDATE application
    SET status = 'REJECTED'
    WHERE vacancy_id = p_vacancy_id
        AND status <> 'OFFER'
    RETURNING application_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION
    get_candidate_leaderboard(
        p_vacancy_id BIGINT,
        p_motivation_weight FLOAT,
        p_soft_skills_weight FLOAT,
        p_pet_projects_weight FLOAT,
        p_job_exp_weight FLOAT,
        p_hard_skills_weight FLOAT,
        p_work_format_weight FLOAT
    )
RETURNS TABLE (
    application_id BIGINT,
    candidate_id BIGINT,
    name VARCHAR,
    vacancy_id BIGINT,
    resume_id BIGINT,
    form_id BIGINT,
    status VARCHAR(20),
    score FLOAT
) AS $$
BEGIN
    RETURN QUERY
        SELECT
            a.application_id, a.candidate_id, p.name, a.vacancy_id, a.resume_id, a.form_id, a.status, (
            sc.motivation * p_motivation_weight +
            sc.soft_skills * p_soft_skills_weight +
            sc.pet_projects * p_pet_projects_weight +
            sc.job_exp * p_job_exp_weight +
            sc.hard_skills * p_hard_skills_weight +
            sc.work_format * p_work_format_weight
            )::FLOAT AS score
        FROM application a
                 JOIN person p ON a.candidate_id = p.person_id
                 JOIN application_score sc ON a.application_id = sc.application_id
        WHERE a.vacancy_id = p_vacancy_id
        ORDER BY score DESC;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE
    schedule_interview(
    p_application_id BIGINT,
    p_scheduled_start TIMESTAMPTZ,
    p_scheduled_end TIMESTAMPTZ
)
AS $$
DECLARE
    v_candidate_id BIGINT;
    v_vacancy_id BIGINT;
    v_teamlead_id BIGINT;
BEGIN
    SELECT a.candidate_id, a.vacancy_id, t.teamlead_id
    INTO v_candidate_id, v_vacancy_id, v_teamlead_id
    FROM application a
             JOIN vacancy v ON a.vacancy_id = v.vacancy_id
             JOIN team t ON v.team_id = t.team_id
    WHERE a.application_id = p_application_id;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Отклик с ID % не найден', p_application_id;
    END IF;

    IF EXISTS (
        SELECT 1 FROM interview
        WHERE teamlead_id = v_teamlead_id
          AND status = 'SCHEDULED'
          AND scheduled_end > p_scheduled_start
          AND p_scheduled_end > scheduled_start
    ) THEN
        RAISE EXCEPTION 'У тимлида есть  собеседование в интервал % - %', p_scheduled_start, p_scheduled_end;
    END IF;

    INSERT INTO interview (candidate_id, teamlead_id, vacancy_id, scheduled_start, scheduled_end, status)
    VALUES (v_candidate_id, v_teamlead_id, v_vacancy_id, p_scheduled_start, p_scheduled_end, 'SCHEDULED');
    UPDATE application SET status = 'INTERVIEW' WHERE application_id = p_application_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION
    check_teamlead_id()
RETURNS TRIGGER
AS $$
DECLARE
    v_role VARCHAR(20);
BEGIN
    SELECT p.role INTO v_role FROM person p WHERE p.person_id = NEW.teamlead_id;
    IF v_role <> 'TEAM_LEAD' THEN
        RAISE EXCEPTION 'Персона - не тимлид: %', NEW.teamlead_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_check_team_teamlead_id
    BEFORE INSERT OR UPDATE ON team
    FOR EACH ROW
EXECUTE FUNCTION check_teamlead_id();

CREATE OR REPLACE FUNCTION
    check_hr_id()
RETURNS TRIGGER
AS $$
DECLARE
    v_role VARCHAR(20);
BEGIN
    SELECT p.role INTO v_role FROM person p WHERE p.person_id = NEW.hr_id;
    IF v_role <> 'HR' THEN
        RAISE EXCEPTION 'Персона - не HR: %', NEW.hr_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_check_application_response_hr_id
    BEFORE INSERT OR UPDATE ON application_response
    FOR EACH ROW
EXECUTE FUNCTION check_hr_id();

CREATE OR REPLACE FUNCTION
    check_candidate_id()
RETURNS TRIGGER
AS $$
DECLARE
    v_role VARCHAR(20);
BEGIN
    SELECT p.role INTO v_role FROM person p WHERE p.person_id = NEW.candidate_id;
    IF v_role <> 'CANDIDATE' THEN
        RAISE EXCEPTION 'Персона - не кандидат: %', NEW.candidate_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_check_application_candidate_id
    BEFORE INSERT OR UPDATE ON application
    FOR EACH ROW
EXECUTE FUNCTION check_candidate_id();

CREATE OR REPLACE TRIGGER trg_check_interview_candidate_id
    BEFORE INSERT OR UPDATE ON interview
    FOR EACH ROW
EXECUTE FUNCTION check_candidate_id();

CREATE OR REPLACE FUNCTION
    refresh_application_score_for(
        p_application_id BIGINT
    )
RETURNS VOID
AS $$
BEGIN
    INSERT INTO application_score (
        application_id,
        motivation,
        soft_skills,
        pet_projects,
        job_exp,
        hard_skills,
        work_format
    )
    SELECT
        application_id,
        ROUND(AVG(motivation))::SMALLINT AS motivation,
        ROUND(AVG(soft_skills))::SMALLINT AS soft_skills,
        ROUND(AVG(pet_projects))::SMALLINT AS pet_projects,
        ROUND(AVG(job_exp))::SMALLINT AS job_exp,
        ROUND(AVG(hard_skills))::SMALLINT AS hard_skills,
        ROUND(AVG(work_format))::SMALLINT AS work_format
    FROM application_response
    WHERE application_id = p_application_id
    GROUP BY application_id
    ON CONFLICT (application_id) DO UPDATE SET
        motivation = EXCLUDED.motivation,
        soft_skills = EXCLUDED.soft_skills,
        pet_projects = EXCLUDED.pet_projects,
        job_exp = EXCLUDED.job_exp,
        hard_skills = EXCLUDED.hard_skills,
        work_format = EXCLUDED.work_format;

    IF NOT FOUND THEN
        DELETE FROM application_score WHERE application_id = p_application_id;
    END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION
    refresh_application_score()
RETURNS TRIGGER
AS $$
BEGIN
    IF TG_OP IN ('INSERT', 'UPDATE') THEN
        PERFORM refresh_application_score_for(NEW.application_id);
    END IF;

    IF TG_OP = 'DELETE' THEN
        PERFORM refresh_application_score_for(OLD.application_id);
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_refresh_application_score
    AFTER INSERT OR UPDATE OR DELETE ON application_response
    FOR EACH ROW
EXECUTE FUNCTION refresh_application_score();

CREATE OR REPLACE FUNCTION
    increment_attempt_counter()
RETURNS TRIGGER
AS $$
BEGIN
    SELECT COALESCE(MAX(attempt), 0) + 1 INTO NEW.attempt FROM submission
    WHERE sender_id = NEW.sender_id AND contest_id = NEW.contest_id AND task_id = NEW.task_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_increment_attempt_counter
    BEFORE INSERT ON submission
    FOR EACH ROW
EXECUTE FUNCTION increment_attempt_counter();

CREATE OR REPLACE FUNCTION
    check_application_for_open_vacancy()
RETURNS TRIGGER
AS $$
DECLARE
    v_status VARCHAR;
BEGIN
    SELECT status INTO v_status FROM vacancy
    WHERE vacancy_id = NEW.vacancy_id;
    IF v_status <> 'OPEN' THEN
        RAISE EXCEPTION 'Вакансия закрыта: %', NEW.vacancy_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_check_application_for_open_vacancy
    BEFORE INSERT ON application
    FOR EACH ROW
EXECUTE FUNCTION check_application_for_open_vacancy();

CREATE OR REPLACE FUNCTION
    set_submission_status_from_response()
RETURNS TRIGGER
AS $$
DECLARE
    v_status VARCHAR;
BEGIN
    IF NEW.verdict = 'OK' THEN
        v_status = 'FINISHED';
    ELSE
        v_status = 'FAILED';
    END IF;

    UPDATE submission
    SET status = v_status
    WHERE submission_id = NEW.submission_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_set_submission_status_from_response
    AFTER INSERT on submission_response
    FOR EACH ROW
EXECUTE FUNCTION set_submission_status_from_response();

CREATE OR REPLACE PROCEDURE
    archive_vacancies()
AS $$
BEGIN
    UPDATE vacancy
    SET status = 'ARCHIVE'
    WHERE status = 'CLOSED'
        AND NOW() - closed_at >= INTERVAL '6 Months';
END;
$$ LANGUAGE plpgsql;

DO $$
BEGIN
    PERFORM cron.schedule(
        'archive_vacancies',
        '0 0 * * *',
        'CALL archive_vacancies();'
    );
EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'pg_cron scheduling skipped: %', SQLERRM;
END $$;
