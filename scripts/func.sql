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