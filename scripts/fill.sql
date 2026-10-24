INSERT INTO company (company_id, name, creation_date) VALUES
(1, 'Яндекс', '2000-09-23'),
(2, 'ВКонтакте', '2006-10-10'),
(3, 'Т-Банк', '2006-12-24');

INSERT INTO person (person_id, name, email, password_hash, role, company_id, team_id, github_id) VALUES
(1, 'Иван Иванов', 'ivan.ivanov@yandex.team', '$2b$12$e8w...hash1', 'TEAM_LEAD', 1, NULL, 10001),
(2, 'Петр Петров', 'petr.petrov@vk.team', '$2b$12$e8w...hash2', 'TEAM_LEAD', 2, NULL, 10002),
(3, 'Анна Смирнова', 'anna.smirnova@tbank.ru', '$2b$12$e8w...hash3', 'HR', 3, NULL, NULL),
(4, 'Елена Васильева', 'elena.vasilieva@yandex.team', '$2b$12$e8w...hash4', 'HR', 1, NULL, NULL),
(5, 'Сергей Сидоров', 'sergey.sidorov@yandex.team', '$2b$12$e8w...hash5', 'DEVELOPER', 1, NULL, 10003),
(6, 'Алексей Кузнецов', 'alex.kuznetsov@gmail.com', '$2b$12$e8w...hash6', 'CANDIDATE', NULL, NULL, 20001),
(7, 'Дмитрий Попов', 'dmitry.popov@mail.ru', '$2b$12$e8w...hash7', 'CANDIDATE', NULL, NULL, 20002),
(8, 'Ольга Николаева', 'olga.nikolaeva@gmail.com', '$2b$12$e8w...hash8', 'CANDIDATE', NULL, NULL, 20003),
(9, 'Администратор Системы', 'admin@itmo-team.ru', '$2b$12$e8w...hash9', 'ADMIN', NULL, NULL, NULL);

INSERT INTO team (team_id, tl_id, name, company_id) VALUES
(1, 1, 'Поисковая платформа', 1),
(2, 2, 'Инфраструктура ленты', 2);

UPDATE person SET team_id = 1 WHERE person_id IN (1, 5);
UPDATE person SET team_id = 2 WHERE person_id = 2;

INSERT INTO coder_report (report_id, coder_id, count_opened_pr, count_closed_pr, count_reviewed_pr, count_commits) VALUES
(1, 1, 15, 12, 34, 85),
(2, 5, 8, 6, 20, 42),
(3, 6, 5, 4, 10, 28),
(4, 7, 2, 2, 3, 14);

INSERT INTO city (city_id, name) VALUES
(1, 'Санкт-Петербург'),
(2, 'Москва'),
(3, 'Новосибирск'),
(4, 'Екатеринбург');

INSERT INTO vacancy (vacancy_id, title, description, team_id, city_id, status, salary) VALUES
(1, 'Senior Backend Developer', 'Разработка сервисов поиска на Kotlin/Java и оптимизация работы с распределенными хранилищами.', 1, 1, 'OPEN', 320000),
(2, 'Infrastructure Engineer', 'Эксплуатация, мониторинг и масштабирование высоконагруженных распределенных систем.', 2, 2, 'OPEN', 250000),
(3, 'Junior Kotlin Developer', 'Разработка внутренних инструментов и автоматизация тестирования команды поиска.', 1, 1, 'CLOSED', 120000);

INSERT INTO vacancy_city (vacancy_id, city_id) VALUES
(1, 1),
(1, 2),
(2, 2),
(2, 3),
(3, 1);

INSERT INTO skill (skill_id, name) VALUES
(1, 'Java'),
(2, 'Kotlin'),
(3, 'PostgreSQL'),
(4, 'Docker'),
(5, 'Kubernetes'),
(6, 'Algorithms'),
(7, 'Spring Boot');

INSERT INTO vacancy_skill (vacancy_id, skill_id) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 7),
(2, 3),
(2, 4),
(2, 5),
(3, 2),
(3, 6);

INSERT INTO resume (resume_id, s3key, filename) VALUES
(1, 'resumes/2026/alex_kuznetsov_cv.pdf', 'alex_kuznetsov_cv.pdf'),
(2, 'resumes/2026/dmitry_popov_cv.pdf', 'dmitry_popov_cv.pdf'),
(3, 'resumes/2026/olga_nikolaeva_cv.pdf', 'olga_nikolaeva_cv.pdf');

INSERT INTO form (form_id, university, motivation, pet_projects, job_exp, hard_skills, work_format) VALUES
(1, 'Университет ИТМО', 'Хочу создавать надежные распределенные сервисы в сильной инженерной команде.', 'tg-bot-schedule, custom-cache-lib', '1.5 года бэкенд разработки на Java/Spring', 'Java, Spring Boot, PostgreSQL, Docker', 'HYBRID'),
(2, 'СПбГУ', 'Интересны вопросы отказоустойчивости, контейнеризации и мониторинга инфраструктуры.', 'mini-k8s-operator, raft-consensus-impl', '1 год системного администрирования Linux', 'Go, Linux, Docker, Kubernetes, Bash', 'OFFICE'),
(3, 'МГТУ им. Баумана', 'Хочу развиваться в промышленной разработке на Kotlin и высоконагруженных сервисах.', 'algos-visualizer, kotlin-async-chat', 'Учебные проекты и стажировка 6 месяцев', 'Kotlin, Coroutines, Git, SQL', 'DISTANT');

INSERT INTO form_skill (form_id, skill_id) VALUES
(1, 1),
(1, 3),
(1, 4),
(1, 7),
(2, 3),
(2, 4),
(2, 5),
(3, 2),
(3, 6);

INSERT INTO application (application_id, person_id, vacancy_id, resume_id, form_id, status) VALUES
(1, 6, 1, 1, 1, 'INTERVIEW'),
(2, 7, 2, 2, 2, 'CONTEST'),
(3, 8, 3, 3, 3, 'REJECTED');

INSERT INTO application_response (response_id, application_id, hr_id, motivation, soft_skills, pet_projects, job_exp, hard_skills, work_format) VALUES
(1, 1, 4, 95, 90, 85, 80, 92, 90),
(2, 2, 3, 85, 80, 90, 75, 88, 85);

INSERT INTO application_score (application_id, motivation, soft_skills, pet_projects, job_exp, hard_skills, work_format) VALUES
(1, 95, 90, 85, 80, 92, 90),
(2, 85, 80, 90, 75, 88, 85);

INSERT INTO interview (interview_id, person_id, teamlead_id, vacancy_id, video_s3key, scheduled_time, status) VALUES
(1, 6, 1, 1, 'videos/interviews/2026/interview_6_1.mp4', '2026-11-01 15:00:00+03', 'COMPLETED'),
(2, 7, 2, 2, NULL, '2026-11-05 18:00:00+03', 'SCHEDULED');

INSERT INTO interview_feedback (feedback_id, interview_id, review, score, decision) VALUES
(1, 1, 'Кандидат продемонстрировал глубокое понимание архитектуры JVM, принципов работы PostgreSQL и многопоточности.', 9, 'HIRE');

INSERT INTO contest (contest_id, scheduled_time, duration) VALUES
(1, '2026-10-28 18:00:00+03', INTERVAL '2 hours'),
(2, '2026-11-03 14:00:00+03', INTERVAL '3 hours');

INSERT INTO task (task_id, author_id, name, description, time_limit, memory_limit) VALUES
(1, 1, 'Сумма подотрезка', 'Дана последовательность целых чисел. Требуется быстро отвечать на запросы суммы элементов на отрезке [L, R].', 1000, 256),
(2, 2, 'Балансировщик запросов', 'Реализуйте алгоритм взвешенного распределения запросов (Weighted Round-Robin) между доступными серверами.', 2000, 512);

INSERT INTO test (test_id, task_id, input_data, output_data, is_hidden) VALUES
(1, 1, '5\n1 2 3 4 5\n1 3', '6', FALSE),
(2, 1, '3\n-1 -2 -3\n1 2', '-3', TRUE),
(3, 2, '3\nsrv1 1\nsrv2 2\nsrv3 1\n4', 'srv2', FALSE);

INSERT INTO language (language_id, name, version) VALUES
(1, 'Java', '21'),
(2, 'Kotlin', '2.0'),
(3, 'Python', '3.12'),
(4, 'C++', '20');

INSERT INTO submission (submission_id, sender_id, contest_id, task_id, language_id, snapshot_s3key, status, verdict, execution_time, memory_used, attempt, ai_score) VALUES
(1, 6, 1, 1, 2, 'submissions/2026/sub_alex_task1_att1.kt', 'FINISHED', 'OK', 145, 45, 1, 100),
(2, 7, 1, 1, 1, 'submissions/2026/sub_dmitry_task1_att1.java', 'FINISHED', 'WRONG_ANSWER', 160, 50, 1, 40),
(3, 7, 1, 1, 1, 'submissions/2026/sub_dmitry_task1_att2.java', 'FINISHED', 'OK', 152, 48, 2, 95);

INSERT INTO runner_server (runner_server_id, ip) VALUES
(1, '10.100.0.1'),
(2, '10.100.0.2');

INSERT INTO contest_task (contest_id, task_id) VALUES
(1, 1),
(1, 2),
(2, 2);

INSERT INTO vacancy_contest (vacancy_id, contest_id) VALUES
(1, 1),
(2, 2);

INSERT INTO contest_participant (contest_id, person_id) VALUES
(1, 6),
(1, 7);

SELECT setval('company_company_id_seq', (SELECT MAX(company_id) FROM company));
SELECT setval('person_person_id_seq', (SELECT MAX(person_id) FROM person));
SELECT setval('team_team_id_seq', (SELECT MAX(team_id) FROM team));
SELECT setval('coder_report_report_id_seq', (SELECT MAX(report_id) FROM coder_report));
SELECT setval('city_city_id_seq', (SELECT MAX(city_id) FROM city));
SELECT setval('vacancy_vacancy_id_seq', (SELECT MAX(vacancy_id) FROM vacancy));
SELECT setval('skill_skill_id_seq', (SELECT MAX(skill_id) FROM skill));
SELECT setval('resume_resume_id_seq', (SELECT MAX(resume_id) FROM resume));
SELECT setval('form_form_id_seq', (SELECT MAX(form_id) FROM form));
SELECT setval('application_application_id_seq', (SELECT MAX(application_id) FROM application));
SELECT setval('application_response_response_id_seq', (SELECT MAX(response_id) FROM application_response));
SELECT setval('interview_interview_id_seq', (SELECT MAX(interview_id) FROM interview));
SELECT setval('interview_feedback_feedback_id_seq', (SELECT MAX(feedback_id) FROM interview_feedback));
SELECT setval('contest_contest_id_seq', (SELECT MAX(contest_id) FROM contest));
SELECT setval('task_task_id_seq', (SELECT MAX(task_id) FROM task));
SELECT setval('test_test_id_seq', (SELECT MAX(test_id) FROM test));
SELECT setval('language_language_id_seq', (SELECT MAX(language_id) FROM language));
SELECT setval('submission_submission_id_seq', (SELECT MAX(submission_id) FROM submission));
SELECT setval('runner_server_runner_server_id_seq', (SELECT MAX(runner_server_id) FROM runner_server));
