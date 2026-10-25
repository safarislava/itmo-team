CREATE EXTENSION IF NOT EXISTS pg_cron;

CREATE OR REPLACE FUNCTION
    match_vacancies(
    p_skill_ids BIGINT[],
    p_city_id BIGINT DEFAULT NULL,
    p_min_salary INTEGER DEFAULT NULL,
    p_max_salary INTEGER DEFAULT NULL
) RETURNS TABLE (
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
) RETURNS TABLE (
    application_id BIGINT,
    person_id BIGINT,
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
            a.application_id, a.person_id, p.name, a.vacancy_id, a.resume_id, a.form_id, a.status, (
            sc.motivation * p_motivation_weight +
            sc.soft_skills * p_soft_skills_weight +
            sc.pet_projects * p_pet_projects_weight +
            sc.job_exp * p_job_exp_weight +
            sc.hard_skills * p_hard_skills_weight +
            sc.work_format * p_work_format_weight
            )::FLOAT AS score
        FROM application a
                 JOIN person p ON a.person_id = p.person_id
                 JOIN application_score sc ON a.application_id = sc.application_id
        WHERE a.vacancy_id = p_vacancy_id
        ORDER BY score DESC;
END;
$$ LANGUAGE plpgsql;