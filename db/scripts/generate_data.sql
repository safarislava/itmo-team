-- !!! COMPLETELY VIBE-CODED POS, USE WITH CAUTION !!!


-- Скрипт нужно выполнять целиком в одной сессии (например, psql -f generate_data.sql).
-- Используются временные таблицы и функции. Объем данных задается в gen_params,
-- генератор случайных чисел зафиксирован через setseed, чтобы данные были воспроизводимыми.
--
-- Порядок вставки учитывает триггеры из func.sql:
--   * отклики создаются, пока вакансии открыты, и только после этого вакансии
--     переводятся в CLOSED / ARCHIVE;
--   * номера попыток и статусы посылок проставляются явно, соответствующие
--     триггеры на время вставки отключаются.

BEGIN;

SELECT setseed(0.69);

CREATE TEMP TABLE gen_params ON COMMIT DROP AS
SELECT
    200    AS companies,
    2000   AS team_leads,
    1000   AS hrs,
    200    AS managers,
    10000  AS developers,
    100000 AS candidates,
    20000  AS vacancies,
    3000   AS tasks;


-- ---------------------------------------------------------------------
-- Вспомогательные функции
-- ---------------------------------------------------------------------

CREATE OR REPLACE FUNCTION pg_temp.pick(arr ANYARRAY) RETURNS ANYELEMENT
    LANGUAGE sql VOLATILE AS $$
SELECT arr[1 + floor(random() * cardinality(arr))::int]
$$;

-- Заглавные буквы переводятся явно: lower() не работает с кириллицей в локали C
CREATE OR REPLACE FUNCTION pg_temp.translit(s TEXT) RETURNS TEXT
    LANGUAGE sql IMMUTABLE AS $$
SELECT translate(
               replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(
                                                                                               lower(translate(s, 'АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ', 'абвгдеёжзийклмнопрстуфхцчшщъыьэюя')),
                                                                                               'щ', 'shch'), 'ж', 'zh'), 'х', 'kh'), 'ц', 'ts'), 'ч', 'ch'),
                                                       'ш', 'sh'), 'ю', 'yu'), 'я', 'ya'), 'ё', 'e'), 'й', 'y'),
               'абвгдезиклмнопрстуфыэ ьъ',
               'abvgdeziklmnoprstufye-'
       )
$$;

CREATE OR REPLACE FUNCTION pg_temp.random_name() RETURNS TEXT
    LANGUAGE sql VOLATILE AS $$
SELECT CASE WHEN is_female THEN female || ' ' || surname || 'а' ELSE male || ' ' || surname END
FROM (
         SELECT
             random() < 0.5 AS is_female,
             pg_temp.pick(ARRAY[
                 'Александр', 'Дмитрий', 'Максим', 'Сергей', 'Андрей', 'Алексей', 'Артем', 'Илья',
                 'Кирилл', 'Михаил', 'Никита', 'Матвей', 'Роман', 'Егор', 'Арсений', 'Иван',
                 'Денис', 'Евгений', 'Даниил', 'Тимофей', 'Владислав', 'Игорь', 'Владимир', 'Павел',
                 'Руслан', 'Марк', 'Константин', 'Тимур', 'Олег', 'Ярослав', 'Антон', 'Николай',
                 'Глеб', 'Савелий', 'Вадим', 'Степан', 'Юрий', 'Богдан', 'Артур', 'Георгий'
                 ]) AS male,
             pg_temp.pick(ARRAY[
                 'Анастасия', 'Мария', 'Анна', 'Виктория', 'Екатерина', 'Наталья', 'Марина', 'Полина',
                 'Дарья', 'Алина', 'Ксения', 'Елизавета', 'Валерия', 'Ольга', 'Татьяна', 'Юлия',
                 'Елена', 'Ирина', 'Софья', 'Вероника', 'Алиса', 'Кристина', 'Светлана', 'Александра',
                 'Арина', 'Ангелина', 'Василиса', 'Варвара', 'Диана', 'Яна', 'Милана', 'Ульяна',
                 'Евгения', 'Маргарита', 'Людмила', 'Карина', 'Надежда', 'Вера', 'Таисия', 'Злата'
                 ]) AS female,
             pg_temp.pick(ARRAY[
                 'Иванов', 'Смирнов', 'Кузнецов', 'Попов', 'Васильев', 'Петров', 'Соколов', 'Михайлов',
                 'Новиков', 'Федоров', 'Морозов', 'Волков', 'Алексеев', 'Лебедев', 'Семенов', 'Егоров',
                 'Павлов', 'Козлов', 'Степанов', 'Николаев', 'Орлов', 'Андреев', 'Макаров', 'Никитин',
                 'Захаров', 'Зайцев', 'Соловьев', 'Борисов', 'Яковлев', 'Григорьев', 'Романов', 'Воробьев',
                 'Сергеев', 'Фролов', 'Александров', 'Дмитриев', 'Королев', 'Гусев', 'Киселев', 'Ильин',
                 'Максимов', 'Поляков', 'Сорокин', 'Виноградов', 'Ковалев', 'Белов', 'Медведев', 'Антонов',
                 'Тарасов', 'Жуков', 'Баранов', 'Филиппов', 'Комаров', 'Давыдов', 'Беляев', 'Герасимов',
                 'Богданов', 'Осипов', 'Сидоров', 'Матвеев', 'Титов', 'Марков', 'Миронов', 'Крылов',
                 'Куликов', 'Карпов', 'Власов', 'Мельников', 'Денисов', 'Гаврилов', 'Тихонов', 'Казаков',
                 'Афанасьев', 'Данилов', 'Савельев', 'Тимофеев', 'Фомин', 'Чернов', 'Абрамов', 'Мартынов',
                 'Ефимов', 'Федотов', 'Щербаков', 'Назаров', 'Калинин', 'Исаев', 'Чернышев', 'Быков',
                 'Маслов', 'Родионов', 'Коновалов', 'Лазарев', 'Воронин', 'Климов', 'Филатов', 'Пономарев',
                 'Голубев', 'Кудрявцев', 'Прохоров', 'Наумов'
                 ]) AS surname
     ) t
$$;

CREATE OR REPLACE FUNCTION pg_temp.make_email(full_name TEXT, id BIGINT, domain TEXT) RETURNS TEXT
    LANGUAGE sql IMMUTABLE AS $$
SELECT pg_temp.translit(split_part(full_name, ' ', 1)) || '.'
           || pg_temp.translit(split_part(full_name, ' ', 2)) || '.' || id || '@' || domain
$$;

CREATE OR REPLACE FUNCTION pg_temp.fake_bcrypt() RETURNS TEXT
    LANGUAGE sql VOLATILE AS $$
SELECT '$2b$12$' || substr(md5(random()::text) || md5(random()::text), 1, 53)
$$;

CREATE OR REPLACE FUNCTION pg_temp.score(base INT) RETURNS SMALLINT
    LANGUAGE sql VOLATILE AS $$
SELECT GREATEST(0, LEAST(100, base - 15 + floor(random() * 31)::int))::smallint
$$;


-- ---------------------------------------------------------------------
-- Справочники для генерации
-- ---------------------------------------------------------------------

CREATE TEMP TABLE gen_role ON COMMIT DROP AS
SELECT * FROM (VALUES
                   (1,  'Backend Developer',       ARRAY['Java', 'Spring Boot', 'PostgreSQL']),
                   (2,  'Java Developer',          ARRAY['Java', 'Spring Boot', 'Hibernate']),
                   (3,  'Kotlin Developer',        ARRAY['Kotlin', 'Spring Boot', 'PostgreSQL']),
                   (4,  'Python Developer',        ARRAY['Python', 'Django', 'PostgreSQL']),
                   (5,  'Go Developer',            ARRAY['Go', 'gRPC', 'PostgreSQL']),
                   (6,  'C++ Developer',           ARRAY['C++', 'Algorithms', 'Linux']),
                   (7,  'Frontend Developer',      ARRAY['TypeScript', 'React', 'JavaScript']),
                   (8,  'Fullstack Developer',     ARRAY['TypeScript', 'Node.js', 'React']),
                   (9,  'Android Developer',       ARRAY['Kotlin', 'Android SDK']),
                   (10, 'iOS Developer',           ARRAY['Swift', 'iOS SDK']),
                   (11, 'DevOps Engineer',         ARRAY['Docker', 'Kubernetes', 'Terraform', 'CI/CD']),
                   (12, 'SRE Engineer',            ARRAY['Linux', 'Kubernetes', 'Prometheus', 'Grafana']),
                   (13, 'Infrastructure Engineer', ARRAY['Linux', 'Ansible', 'Docker']),
                   (14, 'Data Engineer',           ARRAY['Python', 'Spark', 'Airflow', 'SQL']),
                   (15, 'Data Scientist',          ARRAY['Python', 'Pandas', 'Machine Learning']),
                   (16, 'ML Engineer',             ARRAY['Python', 'PyTorch', 'Machine Learning']),
                   (17, 'QA Engineer',             ARRAY['SQL', 'REST API']),
                   (18, 'QA Automation Engineer',  ARRAY['Python', 'Selenium', 'CI/CD']),
                   (19, 'Database Administrator',  ARRAY['PostgreSQL', 'SQL', 'Linux']),
                   (20, 'Security Engineer',       ARRAY['Linux', 'Nginx', 'Kubernetes'])
              ) v(n, title, skills);

CREATE TEMP TABLE gen_topic ON COMMIT DROP AS
SELECT * FROM (VALUES
                   (1,  'Сумма на отрезке', 'Дан массив из n целых чисел. Ответьте на q запросов суммы элементов на отрезке [l, r].'),
                   (2,  'Кратчайший путь', 'Дан взвешенный ориентированный граф. Найдите длины кратчайших путей из вершины s до всех остальных вершин.'),
                   (3,  'Наибольшая возрастающая подпоследовательность', 'Найдите длину наибольшей строго возрастающей подпоследовательности заданной последовательности.'),
                   (4,  'Скобочная последовательность', 'Проверьте, является ли строка из скобок трех типов правильной скобочной последовательностью.'),
                   (5,  'LRU-кэш', 'Реализуйте кэш фиксированной емкости с вытеснением давно неиспользуемых элементов и операциями get/put за O(1).'),
                   (6,  'Порядок сборки модулей', 'Дан ориентированный граф зависимостей. Выведите порядок сборки модулей или сообщите о наличии цикла.'),
                   (7,  'Рюкзак', 'Даны n предметов с весами и стоимостями. Найдите максимальную стоимость предметов, помещающихся в рюкзак вместимости W.'),
                   (8,  'Уникальные пользователи', 'Дан поток событий входа. Для каждого запроса определите число уникальных пользователей за последние k минут.'),
                   (9,  'Слияние интервалов', 'Дан набор интервалов бронирования. Объедините пересекающиеся интервалы и выведите результат.'),
                   (10, 'Медиана потока', 'Числа поступают по одному. После каждого числа выведите текущую медиану.'),
                   (11, 'Редакционное расстояние', 'Найдите минимальное число вставок, удалений и замен символов, превращающих одну строку в другую.'),
                   (12, 'Система непересекающихся множеств', 'Обработайте запросы объединения множеств и проверки принадлежности двух элементов одному множеству.'),
                   (13, 'Ограничитель запросов', 'Реализуйте ограничитель частоты запросов по алгоритму token bucket и обработайте последовательность запросов.'),
                   (14, 'Поиск подстроки', 'Найдите все вхождения шаблона в текст за линейное время.'),
                   (15, 'Минимальное остовное дерево', 'Дан связный неориентированный граф. Найдите вес минимального остовного дерева.'),
                   (16, 'Планировщик задач', 'Дан список задач с дедлайнами и длительностями. Составьте расписание, минимизирующее суммарную просрочку.'),
                   (17, 'Разбор логов', 'Дан журнал запросов веб-сервера. Найдите k самых частых IP-адресов и среднее время ответа.'),
                   (18, 'Пропускная способность канала', 'Определите минимальную пропускную способность канала, при которой все пакеты будут доставлены за t секунд.'),
                   (19, 'Кольцевой буфер', 'Реализуйте кольцевой буфер фиксированного размера с операциями push, pop и peek.'),
                   (20, 'Количество островов', 'Дана карта из нулей и единиц. Посчитайте количество связных областей из единиц.'),
                   (21, 'Обход дерева по уровням', 'Дано бинарное дерево. Выведите его вершины по уровням.'),
                   (22, 'Консистентное хеширование', 'Распределите ключи между серверами с помощью консистентного хеширования и обработайте добавление и удаление серверов.'),
                   (23, 'Самый длинный палиндром', 'Найдите самую длинную подстроку-палиндром в заданной строке.'),
                   (24, 'Числа Фибоначчи', 'Вычислите n-е число Фибоначчи по модулю 10^9 + 7 для n до 10^18.')
              ) v(n, name, statement);


-- ---------------------------------------------------------------------
-- Компании, сотрудники, кандидаты, команды
-- ---------------------------------------------------------------------

INSERT INTO company (name, creation_date)
SELECT
    (ARRAY['Север', 'Гранит', 'Вектор', 'Кварц', 'Орбита', 'Сигма', 'Нейро', 'Квант', 'Пиксель', 'Атлас',
        'Борей', 'Меридиан', 'Нова', 'Вершина', 'Протон', 'Ладога', 'Сфера', 'Импульс', 'Логос', 'Аврора'])[1 + (i - 1) % 20]
        || ' ' ||
    (ARRAY['Софт', 'Технологии', 'Лаб', 'Системс', 'Диджитал', 'Код', 'Девелопмент', 'Тех', 'Данные', 'Облако',
        'Инжиниринг', 'Решения', 'Финтех', 'Медиа', 'Логистик', 'Ритейл', 'Телеком', 'Игры', 'Безопасность', 'Аналитика'])[1 + (i - 1) / 20 % 20]
        || CASE WHEN i > 400 THEN ' ' || i ELSE '' END,
    DATE '1995-01-01' + floor(random() * 10000)::int
FROM generate_series(1, (SELECT companies FROM gen_params)) i;

CREATE TEMP TABLE gen_company ON COMMIT DROP AS
SELECT row_number() OVER (ORDER BY company_id) AS n, company_id, pg_temp.translit(name) || '.ru' AS domain
FROM company;

-- Тимлиды распределены неравномерно: в первых компаниях команд больше
WITH s AS MATERIALIZED (
    SELECT
        nextval('person_person_id_seq') AS person_id,
        pg_temp.random_name() AS name,
        r.role,
        CASE
            WHEN r.role = 'TEAM_LEAD' THEN 1 + floor(power(random(), 2) * (SELECT count(*) FROM gen_company))::int
            WHEN r.role IN ('HR', 'MANAGER') THEN 1 + floor(random() * (SELECT count(*) FROM gen_company))::int
            END AS company_n,
        CASE
            WHEN r.role = 'ADMIN' THEN 'itmo-team.ru'
            ELSE pg_temp.pick(ARRAY['gmail.com', 'gmail.com', 'mail.ru', 'yandex.ru', 'outlook.com', 'icloud.com'])
            END AS public_domain
    FROM gen_params p
             CROSS JOIN LATERAL (VALUES
                                     ('TEAM_LEAD', p.team_leads),
                                     ('HR', p.hrs),
                                     ('MANAGER', p.managers),
                                     ('ADMIN', 3),
                                     ('CANDIDATE', p.candidates)
        ) r(role, cnt)
             CROSS JOIN LATERAL generate_series(1, r.cnt)
)
INSERT INTO person (person_id, name, email, password_hash, role, company_id, github_id)
SELECT
    s.person_id,
    s.name,
    pg_temp.make_email(s.name, s.person_id, COALESCE(c.domain, s.public_domain)),
    pg_temp.fake_bcrypt(),
    s.role,
    c.company_id,
    CASE WHEN s.role = 'TEAM_LEAD' OR (s.role = 'CANDIDATE' AND random() < 0.7) THEN 1000000 + s.person_id END
FROM s
         LEFT JOIN gen_company c ON c.n = s.company_n;

INSERT INTO team (teamlead_id, name, company_id)
SELECT
    tl.person_id,
    (ARRAY['Платежные сервисы', 'Мобильное приложение', 'Рекомендательная система', 'Антифрод', 'Биллинг',
        'Аналитическая платформа', 'Личный кабинет', 'Внутренние инструменты', 'Платформа данных', 'Облачная инфраструктура',
        'Информационная безопасность', 'Голосовой ассистент', 'Маркетплейс', 'Логистика', 'Геосервисы',
        'Видеоплатформа', 'Мессенджер', 'Поиск по товарам', 'CRM', 'Core Banking',
        'Кредитный конвейер', 'Партнерская программа', 'Онбординг', 'Нотификации', 'Авторизация',
        'API Gateway', 'Машинное обучение', 'Компьютерное зрение', 'DevOps платформа', 'Нагрузочное тестирование',
        'Мониторинг', 'Файловое хранилище', 'Контент-платформа', 'Реклама', 'Поддержка клиентов',
        'Веб-фронтенд', 'Дизайн-система', 'Бэк-офис', 'Каталог', 'Корзина и заказы'])[1 + (tl.rn - 1) % 40]
        || CASE WHEN tl.rn > 40 THEN ' ' || (1 + (tl.rn - 1) / 40) ELSE '' END,
    tl.company_id
FROM (
         SELECT p.person_id, p.company_id, row_number() OVER (PARTITION BY p.company_id ORDER BY p.person_id) AS rn
         FROM person p
         WHERE p.role = 'TEAM_LEAD'
           AND NOT EXISTS (SELECT 1 FROM team t WHERE t.teamlead_id = p.person_id)
     ) tl;

UPDATE person p
SET team_id = t.team_id
FROM team t
WHERE t.teamlead_id = p.person_id AND p.team_id IS NULL;

CREATE TEMP TABLE gen_team ON COMMIT DROP AS
SELECT row_number() OVER (ORDER BY t.team_id) AS n, t.team_id, t.name, t.company_id, t.teamlead_id, c.domain
FROM team t
         JOIN gen_company c ON c.company_id = t.company_id;

WITH s AS MATERIALIZED (
    SELECT
        nextval('person_person_id_seq') AS person_id,
        pg_temp.random_name() AS name,
        1 + floor(random() * (SELECT count(*) FROM gen_team))::int AS team_n
    FROM generate_series(1, (SELECT developers FROM gen_params))
)
INSERT INTO person (person_id, name, email, password_hash, role, company_id, team_id, github_id)
SELECT
    s.person_id,
    s.name,
    pg_temp.make_email(s.name, s.person_id, t.domain),
    pg_temp.fake_bcrypt(),
    'DEVELOPER',
    t.company_id,
    t.team_id,
    1000000 + s.person_id
FROM s
         JOIN gen_team t ON t.n = s.team_n;

CREATE TEMP TABLE gen_coder ON COMMIT DROP AS
SELECT row_number() OVER (ORDER BY person_id) AS n, person_id
FROM person
WHERE role IN ('TEAM_LEAD', 'DEVELOPER');

-- Пул HR по компаниям; company_id = 0 - общий пул на случай компании без HR
CREATE TEMP TABLE gen_hr ON COMMIT DROP AS
SELECT company_id, row_number() OVER (PARTITION BY company_id ORDER BY person_id) AS n, person_id
FROM person WHERE role = 'HR'
UNION ALL
SELECT 0, row_number() OVER (ORDER BY person_id), person_id
FROM person WHERE role = 'HR';

CREATE TEMP TABLE gen_hr_size ON COMMIT DROP AS
SELECT company_id, count(*) AS cnt FROM gen_hr GROUP BY company_id;


-- ---------------------------------------------------------------------
-- Отчеты по активности в GitHub
-- ---------------------------------------------------------------------

-- Сотрудники - раз в две недели за последние ~9 месяцев, кандидаты с GitHub - до трех отчетов
WITH src AS MATERIALIZED (
    SELECT person_id, 20 AS periods FROM person WHERE role IN ('TEAM_LEAD', 'DEVELOPER')
    UNION ALL
    SELECT person_id, floor(random() * 4)::int FROM person WHERE role = 'CANDIDATE' AND github_id IS NOT NULL
), r AS MATERIALIZED (
    SELECT src.person_id, w.w, floor(random() * 12)::int AS opened
    FROM src
             CROSS JOIN LATERAL generate_series(1, src.periods) w(w)
)
INSERT INTO coder_report (coder_id, count_opened_pr, count_closed_pr, count_reviewed_pr, count_commits, report_date)
SELECT
    person_id,
    opened,
    floor(opened * (0.5 + random() * 0.5))::int,
    floor(random() * 25)::int,
    opened * 3 + floor(random() * 30)::int,
    NOW() - make_interval(days => w * 14) + random() * INTERVAL '1 day'
FROM r;


-- ---------------------------------------------------------------------
-- Навыки и вакансии
-- ---------------------------------------------------------------------

INSERT INTO skill (name)
SELECT v
FROM unnest(ARRAY[
    'SQL', 'Git', 'Linux', 'Python', 'Go', 'C++', 'C#', 'JavaScript', 'TypeScript', 'React',
    'Vue.js', 'Angular', 'Node.js', 'Rust', 'Swift', 'Android SDK', 'iOS SDK', 'Flutter', 'Hibernate', 'Spring Cloud',
    'Django', 'FastAPI', 'gRPC', 'REST API', 'GraphQL', 'Microservices', 'System Design', 'Redis', 'Kafka', 'RabbitMQ',
    'ClickHouse', 'MongoDB', 'Elasticsearch', 'Nginx', 'Terraform', 'Ansible', 'CI/CD', 'Prometheus', 'Grafana', 'AWS',
    'Machine Learning', 'PyTorch', 'Pandas', 'Spark', 'Airflow', 'Hadoop', 'Selenium', 'JUnit', 'Scrum'
    ]) WITH ORDINALITY u(v, ord)
WHERE NOT EXISTS (SELECT 1 FROM skill s WHERE s.name = u.v)
ORDER BY u.ord;

-- Первые навыки встречаются чаще остальных
CREATE TEMP TABLE gen_skill ON COMMIT DROP AS
SELECT row_number() OVER (ORDER BY skill_id) AS n, skill_id FROM skill;

-- План вакансий: итоговый статус зависит от возраста вакансии
CREATE TEMP TABLE gen_vacancy ON COMMIT DROP AS
WITH s AS MATERIALIZED (
    SELECT
        nextval('vacancy_vacancy_id_seq') AS vacancy_id,
        1 + floor(random() * (SELECT count(*) FROM gen_role))::int AS role_n,
        1 + floor(random() * (SELECT count(*) FROM gen_team))::int AS team_n,
        pg_temp.pick(ARRAY['Junior', 'Middle', 'Middle', 'Middle+', 'Senior', 'Senior', 'Lead']) AS grade,
        NOW() - random() * INTERVAL '730 days' AS created_at,
        random() AS r_status,
        random() AS r_close,
        floor(random() * 4)::int AS extra_skills
    FROM generate_series(1, (SELECT vacancies FROM gen_params))
), c AS (
    SELECT s.*,
           CASE
               WHEN NOW() - s.created_at < INTERVAL '60 days' THEN CASE WHEN s.r_status < 0.15 THEN 'DRAFT' ELSE 'OPEN' END
               WHEN NOW() - s.created_at < INTERVAL '180 days' THEN CASE WHEN s.r_status < 0.5 THEN 'OPEN' ELSE 'CLOSED' END
               ELSE CASE WHEN s.r_status < 0.08 THEN 'OPEN' ELSE 'CLOSED' END
               END AS planned_status
    FROM s
), d AS (
    SELECT c.*,
           CASE WHEN c.planned_status = 'CLOSED' THEN
                    c.created_at + LEAST((NOW() - c.created_at) * (0.1 + 0.85 * c.r_close), INTERVAL '14 days' + c.r_close * INTERVAL '150 days')
               END AS closed_at
    FROM c
)
SELECT
    d.vacancy_id,
    d.role_n,
    d.extra_skills,
    t.team_id,
    t.company_id,
    d.grade || ' ' || r.title AS title,
    format('Команда «%s» ищет специалиста на позицию %s. %s %s',
           t.name,
           d.grade || ' ' || r.title,
           pg_temp.pick(ARRAY[
               'Вы будете проектировать и развивать сервисы с высокой нагрузкой.',
               'Предстоит участвовать в проектировании архитектуры и код-ревью.',
               'Основная задача - развитие ключевого продукта команды и сокращение технического долга.',
               'Работа включает оптимизацию производительности и повышение отказоустойчивости.',
               'Нужно будет автоматизировать процессы разработки, тестирования и выкладки.',
               'Вы будете работать в связке с аналитиками, дизайнерами и продуктовыми менеджерами.',
               'Предстоит запуск нового направления с нуля.',
               'Ждем опыта работы с распределенными системами и умения писать поддерживаемый код.'
               ]),
           pg_temp.pick(ARRAY[
               'Предлагаем ДМС, гибкий график и компенсацию обучения.',
               'Есть возможность удаленной работы и релокации.',
               'Оформление по ТК РФ, ежегодный пересмотр зарплаты.',
               'Поддерживаем участие в конференциях и open source.',
               'Бонусы по итогам года и опционная программа.',
               'Современный офис, спорт и корпоративное обучение.'
               ])
    ) AS description,
    pg_temp.pick(ARRAY[
        'Москва', 'Москва', 'Москва', 'Москва', 'Москва', 'Москва', 'Москва', 'Москва',
        'Санкт-Петербург', 'Санкт-Петербург', 'Санкт-Петербург', 'Санкт-Петербург', 'Санкт-Петербург',
        'Новосибирск', 'Екатеринбург', 'Казань', 'Нижний Новгород', 'Иннополис', 'Самара', 'Краснодар',
        'Пермь', 'Томск', 'Ростов-на-Дону', 'Воронеж', 'Уфа', NULL, NULL
        ]) AS city,
    (CASE WHEN random() >= 0.05 THEN
              round(CASE d.grade
                        WHEN 'Junior' THEN 100000
                        WHEN 'Middle' THEN 190000
                        WHEN 'Middle+' THEN 240000
                        WHEN 'Senior' THEN 310000
                        ELSE 380000
                        END * (0.75 + 0.6 * random()) / 5000) * 5000
        END)::int AS salary,
    d.created_at,
    CASE WHEN d.closed_at IS NOT NULL AND NOW() - d.closed_at >= INTERVAL '6 months' THEN 'ARCHIVE' ELSE d.planned_status END AS final_status,
    d.closed_at
FROM d
         JOIN gen_team t ON t.n = d.team_n
         JOIN gen_role r ON r.n = d.role_n;

-- Пока все вакансии, кроме черновиков, открыты: иначе триггер не даст создать отклики
INSERT INTO vacancy (vacancy_id, title, description, team_id, city, status, salary, created_at, closed_at)
SELECT vacancy_id, title, description, team_id, city,
       CASE WHEN final_status = 'DRAFT' THEN 'DRAFT' ELSE 'OPEN' END,
       salary, created_at, NULL
FROM gen_vacancy;

INSERT INTO vacancy_skill (vacancy_id, skill_id)
SELECT v.vacancy_id, s.skill_id
FROM gen_vacancy v
         JOIN gen_role r ON r.n = v.role_n
         JOIN skill s ON s.name = ANY (r.skills)
ON CONFLICT DO NOTHING;

WITH e AS MATERIALIZED (
    SELECT v.vacancy_id, 1 + floor(power(random(), 1.5) * (SELECT count(*) FROM gen_skill))::int AS skill_n
    FROM gen_vacancy v
             CROSS JOIN LATERAL generate_series(1, v.extra_skills)
)
INSERT INTO vacancy_skill (vacancy_id, skill_id)
SELECT e.vacancy_id, s.skill_id
FROM e
         JOIN gen_skill s ON s.n = e.skill_n
ON CONFLICT DO NOTHING;


-- ---------------------------------------------------------------------
-- Резюме, анкеты и отклики кандидатов
-- ---------------------------------------------------------------------

-- Кандидаты из ручного заполнения уже имеют отклики, их не трогаем
CREATE TEMP TABLE gen_candidate ON COMMIT DROP AS
SELECT
    p.person_id,
    p.name,
    nextval('resume_resume_id_seq') AS resume_id,
    nextval('form_form_id_seq') AS form_id,
    1 + floor(random() * (SELECT count(*) FROM gen_role))::int AS role_n,
    LEAST(15, floor(-ln(1 - random()) * 3)::int) AS app_count,
    floor(random() * 5)::int AS extra_skills
FROM person p
WHERE p.role = 'CANDIDATE'
  AND NOT EXISTS (SELECT 1 FROM application a WHERE a.candidate_id = p.person_id);

INSERT INTO resume (resume_id, s3key, filename)
SELECT resume_id, 'resumes/' || pg_temp.pick(ARRAY['2024', '2025', '2026']) || '/' || filename, filename
FROM (
         SELECT resume_id,
                replace(pg_temp.translit(name), '-', '_') || '_' || person_id || '_cv.'
                    || pg_temp.pick(ARRAY['pdf', 'pdf', 'pdf', 'docx']) AS filename
         FROM gen_candidate
     ) r;

INSERT INTO form (form_id, university, motivation, pet_projects, job_exp, work_format)
SELECT
    form_id,
    pg_temp.pick(ARRAY[
        'Университет ИТМО', 'Университет ИТМО', 'Университет ИТМО', 'СПбГУ', 'МГУ им. М. В. Ломоносова', 'МФТИ',
        'НИУ ВШЭ', 'МГТУ им. Баумана', 'СПбПУ Петра Великого', 'НГУ', 'УрФУ', 'КФУ', 'ТПУ', 'СПбГЭТУ «ЛЭТИ»',
        'НИЯУ МИФИ', 'Университет Иннополис', 'ННГУ им. Лобачевского', 'ЮФУ', 'СамГТУ', NULL, NULL
        ]),
    pg_temp.pick(ARRAY[
        'Хочу развиваться в сильной инженерной команде и работать над продуктом с большой аудиторией.',
        'Интересно решать задачи высокой нагрузки и проектировать отказоустойчивые системы.',
        'Привлекает возможность влиять на архитектуру продукта и работать с современным стеком.',
        'Хочу перейти из учебных проектов в промышленную разработку.',
        'Ищу команду, где ценят качество кода, ревью и автоматизацию.',
        'Нравится продукт компании, пользуюсь им каждый день и хочу сделать его лучше.',
        'Хочу углубиться в распределенные системы и работу с большими данными.',
        'Рассматриваю смену стека и хочу применить свой опыт в новой области.',
        'Интересна работа на стыке разработки и машинного обучения.',
        'Хочу расти до тимлида и участвовать в найме и менторстве.',
        'Привлекает сильное инженерное сообщество и внутренние образовательные программы.',
        NULL
        ]),
    CASE WHEN random() < 0.85 THEN
             pg_temp.pick(ARRAY[
                 'telegram-бот расписания', 'менеджер паролей', 'маркетплейс на микросервисах', 'клон Trello',
                 'парсер вакансий', 'трекер привычек', 'игра на Unity', 'библиотека логирования',
                 'рекомендательная система фильмов', 'HTTP-сервер на C++', 'key-value хранилище', 'чат на WebSocket',
                 'мобильное приложение для заметок', 'визуализатор алгоритмов', 'CI/CD для домашнего сервера'
                 ]) || ', ' ||
             pg_temp.pick(ARRAY[
                 'Kubernetes-оператор', 'компилятор учебного языка', 'торговый бот для биржи', 'сервис сокращения ссылок',
                 'мониторинг умного дома', 'реализация Raft', 'файловое хранилище с шифрованием', 'браузерное расширение',
                 'анализатор логов', 'классификатор изображений'
                 ])
        END,
    pg_temp.pick(ARRAY[
        'Без коммерческого опыта, учебные проекты', 'Стажировка 3 месяца', 'Стажировка 6 месяцев',
        '1 год бэкенд-разработки', '1.5 года фронтенд-разработки', '2 года в продуктовой компании',
        '3 года разработки в банке', '3 года в аутсорсе', '4 года, из них год тимлидом',
        '5 лет в высоконагруженных сервисах', '6 лет, участие в проектировании архитектуры', '2 года фриланса', NULL
        ]),
    pg_temp.pick(ARRAY['OFFICE', 'DISTANT', 'HYBRID', 'HYBRID', NULL])
FROM gen_candidate;

WITH core AS MATERIALIZED (
    SELECT c.form_id, s.skill_id, random() AS r
    FROM gen_candidate c
             JOIN gen_role r ON r.n = c.role_n
             JOIN skill s ON s.name = ANY (r.skills)
)
INSERT INTO form_skill (form_id, skill_id)
SELECT form_id, skill_id FROM core WHERE r < 0.8
ON CONFLICT DO NOTHING;

WITH e AS MATERIALIZED (
    SELECT c.form_id, 1 + floor(power(random(), 1.5) * (SELECT count(*) FROM gen_skill))::int AS skill_n
    FROM gen_candidate c
             CROSS JOIN LATERAL generate_series(1, c.extra_skills)
)
INSERT INTO form_skill (form_id, skill_id)
SELECT e.form_id, s.skill_id
FROM e
         JOIN gen_skill s ON s.n = e.skill_n
ON CONFLICT DO NOTHING;

-- Пул вакансий для откликов: role_n = 0 - все вакансии, иначе вакансии конкретной роли
CREATE TEMP TABLE gen_vacancy_pool ON COMMIT DROP AS
SELECT 0 AS role_n, row_number() OVER (ORDER BY vacancy_id) AS n, vacancy_id
FROM gen_vacancy WHERE final_status <> 'DRAFT'
UNION ALL
SELECT role_n, row_number() OVER (PARTITION BY role_n ORDER BY vacancy_id), vacancy_id
FROM gen_vacancy WHERE final_status <> 'DRAFT';

CREATE TEMP TABLE gen_vacancy_pool_size ON COMMIT DROP AS
SELECT role_n, count(*) AS cnt FROM gen_vacancy_pool GROUP BY role_n;

-- 70% откликов - на вакансии своей роли, остальные - на случайные
WITH a AS MATERIALIZED (
    SELECT
        c.person_id,
        c.resume_id,
        c.form_id,
        CASE WHEN random() < 0.7 THEN c.role_n ELSE 0 END AS pool,
        random() AS r_vacancy,
        random() AS r_status
    FROM gen_candidate c
             CROSS JOIN LATERAL generate_series(1, c.app_count)
)
INSERT INTO application (candidate_id, vacancy_id, resume_id, form_id, status)
SELECT
    a.person_id,
    p.vacancy_id,
    a.resume_id,
    a.form_id,
    CASE
        WHEN v.final_status = 'OPEN' THEN
            CASE
                WHEN a.r_status < 0.35 THEN 'NEW'
                WHEN a.r_status < 0.60 THEN 'REVIEW'
                WHEN a.r_status < 0.75 THEN 'CONTEST'
                WHEN a.r_status < 0.85 THEN 'INTERVIEW'
                WHEN a.r_status < 0.88 THEN 'OFFER'
                ELSE 'REJECTED'
                END
        ELSE CASE WHEN a.r_status < 0.03 THEN 'OFFER' ELSE 'REJECTED' END
        END
FROM a
         JOIN gen_vacancy_pool_size ps ON ps.role_n = a.pool
         JOIN gen_vacancy_pool p ON p.role_n = a.pool AND p.n = 1 + floor(a.r_vacancy * ps.cnt)::int
         JOIN gen_vacancy v ON v.vacancy_id = p.vacancy_id
ON CONFLICT (candidate_id, vacancy_id) DO NOTHING;

-- Теперь вакансии можно перевести в итоговый статус
UPDATE vacancy v
SET status = g.final_status, closed_at = g.closed_at
FROM gen_vacancy g
WHERE g.vacancy_id = v.vacancy_id AND g.final_status IN ('CLOSED', 'ARCHIVE');

-- Какие этапы прошел кандидат (отклоненные кандидаты могли дойти до контеста и интервью)
CREATE TEMP TABLE gen_application ON COMMIT DROP AS
SELECT
    a.application_id,
    a.candidate_id,
    a.vacancy_id,
    a.status,
    v.final_status,
    v.team_id,
    v.company_id,
    v.created_at,
    v.closed_at,
    CASE WHEN a.status = 'NEW' THEN 0 WHEN random() < 0.3 THEN 2 ELSE 1 END AS hr_responses,
    CASE
        WHEN a.status IN ('CONTEST', 'INTERVIEW', 'OFFER') THEN TRUE
        WHEN a.status = 'REJECTED' THEN random() < 0.4
        ELSE FALSE
        END AS reached_contest,
    CASE
        WHEN a.status IN ('INTERVIEW', 'OFFER') THEN TRUE
        WHEN a.status = 'REJECTED' THEN random() < 0.12
        ELSE FALSE
        END AS reached_interview
FROM application a
         JOIN gen_vacancy v ON v.vacancy_id = a.vacancy_id;


-- ---------------------------------------------------------------------
-- Оценки HR
-- ---------------------------------------------------------------------

WITH r AS MATERIALIZED (
    SELECT
        a.application_id,
        a.company_id,
        random() AS r_hr,
        CASE a.status
            WHEN 'OFFER' THEN 85
            WHEN 'INTERVIEW' THEN 78
            WHEN 'CONTEST' THEN 70
            WHEN 'REVIEW' THEN 62
            ELSE 45
            END AS base
    FROM gen_application a
             CROSS JOIN LATERAL generate_series(1, a.hr_responses)
)
INSERT INTO application_response (application_id, hr_id, motivation, soft_skills, pet_projects, job_exp, hard_skills, work_format)
SELECT
    r.application_id,
    h.person_id,
    pg_temp.score(r.base),
    pg_temp.score(r.base),
    pg_temp.score(r.base),
    pg_temp.score(r.base),
    pg_temp.score(r.base),
    pg_temp.score(r.base)
FROM r
         LEFT JOIN gen_hr_size cs ON cs.company_id = r.company_id
         JOIN gen_hr_size gs ON gs.company_id = COALESCE(cs.company_id, 0)
         JOIN gen_hr h ON h.company_id = gs.company_id AND h.n = 1 + floor(r.r_hr * gs.cnt)::int
ON CONFLICT (application_id, hr_id) DO NOTHING;


-- ---------------------------------------------------------------------
-- Собеседования
-- ---------------------------------------------------------------------

-- Предстоящие собеседования - только по открытым вакансиям, прошедшие - в период жизни вакансии
WITH i AS MATERIALIZED (
    SELECT
        a.candidate_id,
        a.vacancy_id,
        t.teamlead_id,
        CASE
            WHEN a.final_status = 'OPEN' AND a.status = 'INTERVIEW' AND random() < 0.6 THEN 'SCHEDULED'
            WHEN a.status <> 'OFFER' AND random() < 0.1 THEN 'CANCELLED'
            ELSE 'COMPLETED'
            END AS status,
        a.created_at,
        COALESCE(a.closed_at, NOW()) - INTERVAL '1 day' AS window_end,
        random() AS r_day,
        make_interval(hours => 10 + floor(random() * 9)::int, mins => pg_temp.pick(ARRAY[0, 30])) AS time_of_day,
        make_interval(mins => pg_temp.pick(ARRAY[30, 60, 60, 90])) AS duration,
        random() AS r_video
    FROM gen_application a
             JOIN team t ON t.team_id = a.team_id
    WHERE a.reached_interview
), s AS (
    SELECT i.*,
           CASE
               WHEN i.status = 'SCHEDULED' THEN date_trunc('day', NOW()) + make_interval(days => 1 + floor(i.r_day * 21)::int)
               ELSE date_trunc('day', LEAST(i.created_at + (i.window_end - i.created_at) * i.r_day, NOW() - INTERVAL '1 day'))
               END + i.time_of_day AS scheduled_start
    FROM i
)
INSERT INTO interview (candidate_id, teamlead_id, vacancy_id, video_s3key, scheduled_start, scheduled_end, status)
SELECT
    candidate_id,
    teamlead_id,
    vacancy_id,
    CASE WHEN status = 'COMPLETED' AND r_video < 0.8 THEN
             'videos/interviews/' || to_char(scheduled_start, 'YYYY') || '/interview_' || candidate_id || '_' || vacancy_id || '.mp4'
        END,
    scheduled_start,
    scheduled_start + duration,
    status
FROM s;

WITH f AS MATERIALIZED (
    SELECT
        i.interview_id,
        CASE
            WHEN a.status = 'OFFER' THEN 'HIRE'
            WHEN a.status = 'REJECTED' THEN 'NO_HIRE'
            WHEN random() < 0.6 THEN 'NEXT_STAGE'
            ELSE 'HIRE'
            END AS decision,
        random() AS r_feedback
    FROM interview i
             JOIN gen_application a ON a.candidate_id = i.candidate_id AND a.vacancy_id = i.vacancy_id
    WHERE i.status = 'COMPLETED'
)
INSERT INTO interview_feedback (interview_id, review, score, decision)
SELECT
    interview_id,
    CASE decision
        WHEN 'HIRE' THEN pg_temp.pick(ARRAY[
            'Кандидат уверенно решил все задачи и хорошо аргументировал архитектурные решения.',
            'Сильные знания основ, чистый код, отличная коммуникация. Рекомендую к найму.',
            'Глубоко понимает работу баз данных и многопоточность, быстро ориентируется в незнакомом коде.',
            'Показал отличные навыки проектирования систем и внимание к деталям.',
            'Хорошо подходит команде: релевантный опыт и высокая мотивация.'
            ])
        WHEN 'NEXT_STAGE' THEN pg_temp.pick(ARRAY[
            'Хорошая база, но есть пробелы в проектировании. Нужна дополнительная секция по system design.',
            'Алгоритмическая часть пройдена, стоит проверить практические навыки на live-coding.',
            'Неплохой уровень, требуется финальное интервью с руководителем направления.',
            'Коммуникация отличная, технические навыки нужно уточнить на следующем этапе.'
            ])
        ELSE pg_temp.pick(ARRAY[
            'Кандидат не справился с базовыми задачами на алгоритмы.',
            'Слабое понимание работы с базами данных и транзакциями.',
            'Опыт не соответствует уровню позиции.',
            'Сложности с объяснением собственных решений, код плохо структурирован.',
            'Не хватает знаний основного стека команды.'
            ])
        END,
    CASE decision
        WHEN 'HIRE' THEN 7 + floor(random() * 4)::int
        WHEN 'NEXT_STAGE' THEN 5 + floor(random() * 4)::int
        ELSE 1 + floor(random() * 5)::int
        END,
    decision
FROM f
WHERE r_feedback < 0.95;


-- ---------------------------------------------------------------------
-- Задачи, тесты, языки, серверы проверки
-- ---------------------------------------------------------------------

INSERT INTO language (name, version) VALUES
                                         ('Java', '17'),
                                         ('Kotlin', '1.9'),
                                         ('Python', '3.11'),
                                         ('C++', '17'),
                                         ('Go', '1.22'),
                                         ('C#', '12'),
                                         ('JavaScript', 'Node 20'),
                                         ('TypeScript', '5.4'),
                                         ('Rust', '1.79'),
                                         ('Swift', '5.10')
ON CONFLICT (name, version) DO NOTHING;

CREATE TEMP TABLE gen_language ON COMMIT DROP AS
SELECT
            row_number() OVER (ORDER BY language_id) AS n,
            language_id,
            CASE name
                WHEN 'Java' THEN 'java'
                WHEN 'Kotlin' THEN 'kt'
                WHEN 'Python' THEN 'py'
                WHEN 'C++' THEN 'cpp'
                WHEN 'Go' THEN 'go'
                WHEN 'C#' THEN 'cs'
                WHEN 'JavaScript' THEN 'js'
                WHEN 'TypeScript' THEN 'ts'
                WHEN 'Rust' THEN 'rs'
                WHEN 'Swift' THEN 'swift'
                ELSE 'txt'
                END AS ext
FROM language;

INSERT INTO runner_server (ip)
SELECT ('10.100.0.' || i)::inet FROM generate_series(3, 20) i
ON CONFLICT (ip) DO NOTHING;

WITH s AS MATERIALIZED (
    SELECT
        i,
        1 + floor(random() * (SELECT count(*) FROM gen_coder))::int AS author_n,
        1 + floor(random() * (SELECT count(*) FROM gen_topic))::int AS topic_n
    FROM generate_series(1, (SELECT tasks FROM gen_params)) i
)
INSERT INTO task (author_id, name, description, time_limit, memory_limit)
SELECT
    c.person_id,
    format('%s #%s', t.name, s.i),
    t.statement,
    pg_temp.pick(ARRAY[500, 1000, 1000, 2000, 2000, 3000, 5000]),
    pg_temp.pick(ARRAY[64, 128, 256, 256, 256, 512])
FROM s
         JOIN gen_coder c ON c.n = s.author_n
         JOIN gen_topic t ON t.n = s.topic_n;

-- Тест: массив случайных чисел, ответ - их сумма; первые два теста открытые
WITH t AS MATERIALIZED (
    SELECT tk.task_id, 5 + floor(random() * 16)::int AS test_count
    FROM task tk
    WHERE NOT EXISTS (SELECT 1 FROM test ts WHERE ts.task_id = tk.task_id)
)
INSERT INTO test (task_id, input_data, output_data, is_hidden)
SELECT
    t.task_id,
    cardinality(x.nums) || E'\n' || array_to_string(x.nums, ' '),
    (SELECT sum(v) FROM unnest(x.nums) v)::text,
    g.j > 2
FROM t
         CROSS JOIN LATERAL generate_series(1, t.test_count) g(j)
         CROSS JOIN LATERAL (
    SELECT array_agg(floor(random() * 2001)::int - 1000) AS nums
    FROM generate_series(1, 3 + (t.task_id + g.j)::int % 15)
    ) x;

CREATE TEMP TABLE gen_task ON COMMIT DROP AS
SELECT row_number() OVER (ORDER BY task_id) AS n, task_id FROM task;


-- ---------------------------------------------------------------------
-- Контесты и посылки
-- ---------------------------------------------------------------------

-- У половины вакансий есть свой контест в период жизни вакансии
CREATE TEMP TABLE gen_contest ON COMMIT DROP AS
SELECT
    nextval('contest_contest_id_seq') AS contest_id,
    v.vacancy_id,
    date_trunc('hour', v.created_at + (COALESCE(v.closed_at, NOW() + INTERVAL '30 days') - v.created_at) * (0.1 + 0.4 * random())) AS scheduled_time,
    make_interval(mins => pg_temp.pick(ARRAY[60, 90, 120, 120, 180, 180, 240, 300])) AS duration,
    2 + floor(random() * 5)::int AS task_count
FROM gen_vacancy v
WHERE v.final_status <> 'DRAFT' AND random() < 0.5;

INSERT INTO contest (contest_id, scheduled_time, duration)
SELECT contest_id, scheduled_time, duration FROM gen_contest;

INSERT INTO vacancy_contest (vacancy_id, contest_id)
SELECT vacancy_id, contest_id FROM gen_contest;

WITH e AS MATERIALIZED (
    SELECT c.contest_id, 1 + floor(random() * (SELECT count(*) FROM gen_task))::int AS task_n
    FROM gen_contest c
             CROSS JOIN LATERAL generate_series(1, c.task_count)
)
INSERT INTO contest_task (contest_id, task_id)
SELECT e.contest_id, t.task_id
FROM e
         JOIN gen_task t ON t.n = e.task_n
ON CONFLICT DO NOTHING;

INSERT INTO contest_participant (contest_id, person_id)
SELECT c.contest_id, a.candidate_id
FROM gen_application a
         JOIN gen_contest c ON c.vacancy_id = a.vacancy_id
WHERE a.reached_contest
ON CONFLICT DO NOTHING;

-- Посылки только по завершившимся контестам; задача решена, если последняя попытка - OK
CREATE TEMP TABLE gen_submission ON COMMIT DROP AS
WITH pt AS MATERIALIZED (
    SELECT
        cp.person_id,
        cp.contest_id,
        ct.task_id,
        c.scheduled_time,
        c.duration,
        t.time_limit,
        t.memory_limit,
        1 + floor(power(random(), 2) * 5)::int AS attempts,
        random() AS r_try,
        random() < 0.65 AS solved
    FROM contest_participant cp
             JOIN gen_contest c ON c.contest_id = cp.contest_id
             JOIN contest_task ct ON ct.contest_id = cp.contest_id
             JOIN task t ON t.task_id = ct.task_id
    WHERE c.scheduled_time + c.duration < NOW()
), s AS MATERIALIZED (
    SELECT
        nextval('submission_submission_id_seq') AS submission_id,
        pt.*,
        g.attempt,
        pt.scheduled_time + pt.duration * ((g.attempt - 1 + random()) / pt.attempts) AS submitted_at,
        1 + floor(power(random(), 1.5) * (SELECT count(*) FROM gen_language))::int AS language_n,
        random() AS r_status
    FROM pt
             CROSS JOIN LATERAL generate_series(1, pt.attempts) g(attempt)
    WHERE pt.r_try < 0.75
)
SELECT
    s.*,
    l.language_id,
    l.ext,
    CASE
        WHEN s.r_status < 0.01 THEN 'QUEUED'
        WHEN s.r_status < 0.015 THEN 'RUNNING'
        WHEN s.solved AND s.attempt = s.attempts THEN 'FINISHED'
        ELSE 'FAILED'
        END AS status,
    CASE
        WHEN s.r_status < 0.015 THEN NULL
        WHEN s.solved AND s.attempt = s.attempts THEN 'OK'
        ELSE pg_temp.pick(ARRAY[
            'WRONG_ANSWER', 'WRONG_ANSWER', 'WRONG_ANSWER', 'WRONG_ANSWER', 'WRONG_ANSWER',
            'TIME_LIMIT', 'TIME_LIMIT', 'RUNTIME_ERROR', 'RUNTIME_ERROR', 'MEMORY_LIMIT', 'COMPILATION_ERROR'
            ])
        END AS verdict
FROM s
         JOIN gen_language l ON l.n = s.language_n;

ALTER TABLE submission DISABLE TRIGGER trg_increment_attempt_counter;

INSERT INTO submission (submission_id, sender_id, contest_id, task_id, language_id, snapshot_s3key, status, attempt, ai_score, submitted_at)
SELECT
    submission_id,
    person_id,
    contest_id,
    task_id,
    language_id,
    format('submissions/%s/sub_%s_%s_%s_att%s.%s', to_char(submitted_at, 'YYYY'), person_id, contest_id, task_id, attempt, ext),
    status,
    attempt,
    CASE WHEN verdict IS NOT NULL AND random() < 0.9 THEN floor(random() * 101)::int END,
    submitted_at
FROM gen_submission;

ALTER TABLE submission ENABLE TRIGGER trg_increment_attempt_counter;

ALTER TABLE submission_response DISABLE TRIGGER trg_set_submission_status_from_response;

INSERT INTO submission_response (submission_id, verdict, execution_time, memory_used, runner_server_id)
SELECT
    submission_id,
    verdict,
    CASE verdict
        WHEN 'COMPILATION_ERROR' THEN NULL
        WHEN 'TIME_LIMIT' THEN time_limit + floor(random() * time_limit / 2)::int
        ELSE floor(random() * time_limit * 0.95)::int
        END,
    CASE verdict
        WHEN 'COMPILATION_ERROR' THEN NULL
        WHEN 'MEMORY_LIMIT' THEN memory_limit + 1 + floor(random() * 64)::int
        ELSE 1 + floor(random() * memory_limit * 0.8)::int
        END,
    pg_temp.pick((SELECT array_agg(runner_server_id) FROM runner_server))
FROM gen_submission
WHERE verdict IS NOT NULL;

ALTER TABLE submission_response ENABLE TRIGGER trg_set_submission_status_from_response;

COMMIT;

-- Актуальная статистика для планировщика
ANALYZE;

SELECT relname AS table_name, n_live_tup AS row_count
FROM pg_stat_user_tables
ORDER BY n_live_tup DESC;