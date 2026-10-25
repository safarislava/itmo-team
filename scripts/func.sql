CREATE EXTENSION IF NOT EXISTS pg_cron;

CREATE OR REPLACE FUNCTION
    match_vacancies(
        p_skill_ids BIGINT[],
        p_city_id BIGINT DEFAULT NULL,
        p_min_salary INTEGER DEFAULT NULL,
        p_max_salary INTEGER DEFAULT NULL
    )
RETURNS TABLE (
    vacancy_id BIGINT,
    title VARCHAR,
    description TEXT,
    city_name VARCHAR,
    salary INTEGER
) AS $$
BEGIN
    RETURN QUERY
    SELECT v.vacancy_id, v.title, v.description, c.name, v.salary FROM vacancy v
    LEFT JOIN city c ON c.city_id = v.city_id
    WHERE v.status = 'OPEN'
        AND (COALESCE(p_skill_ids, '{}') <@ (
            SELECT ARRAY_AGG(vs.skill_id)
            FROM vacancy_skill vs
            WHERE vs.vacancy_id = v.vacancy_id
        ))
        AND (p_min_salary IS NULL OR v.salary >= p_min_salary)
        AND (p_max_salary IS NULL OR v.salary <= p_max_salary)
        AND (p_city_id IS NULL OR EXISTS (
            SELECT 1 FROM vacancy_city vc
            WHERE vc.vacancy_id = v.vacancy_id AND vc.city_id = p_city_id
        ));
END;
$$ LANGUAGE plpgsql;


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

SELECT cron.schedule(
    'archive_vacancies',
    '0 0 * * *',
    'CALL archive_vacancies();'
);

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
    p_scheduled_time TIMESTAMPTZ
)
AS $$
DECLARE
    v_candidate_id BIGINT;
    v_vacancy_id BIGINT;
    v_teamlead_id BIGINT;
BEGIN
    SELECT a.candidate_id, a.vacancy_id, t.tl_id
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
          AND scheduled_time = p_scheduled_time
    ) THEN
        RAISE EXCEPTION 'У тимлида уже назначено собеседование на %', p_scheduled_time;
    END IF;

    INSERT INTO interview (candidate_id, teamlead_id, vacancy_id, scheduled_time, status)
    VALUES (v_candidate_id, v_teamlead_id, v_vacancy_id, p_scheduled_time, 'SCHEDULED');
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
    SELECT p.role INTO v_role FROM person p WHERE p.person_id = NEW.tl_id;
    IF v_role <> 'TEAM_LEAD' THEN
        RAISE EXCEPTION 'Персона - не тимлид: %', NEW.tl_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_team_teamlead_id
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
        RAISE EXCEPTION 'Персона - не HR: %', NEW.tl_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_application_response_hr_id
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
        RAISE EXCEPTION 'Персона - не кандидат: %', NEW.tl_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_application_candidate_id
    BEFORE INSERT OR UPDATE ON application
    FOR EACH ROW
EXECUTE FUNCTION check_candidate_id();

CREATE TRIGGER trg_check_interview_candidate_id
    BEFORE INSERT OR UPDATE ON interview
    FOR EACH ROW
EXECUTE FUNCTION check_candidate_id();


-- TODO триггер обновление application_score
-- TODO триггер автоматического инкремента submission.attemp
-- TODO триггер проверки подачи отклика на закрытую вакансию