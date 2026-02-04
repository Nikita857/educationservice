-- Идемпотентный сидер для администратора
INSERT INTO users (id, version, created_at, updated_at, username, first_name, last_name, active, email, personnel_number, password_hash, role, status)
VALUES ('00000000-0000-0000-0000-000000000001', 0, now(), now(), 'admin', 'Админ', 'Системы', true, 'admin@factory.com', '000001', '$2a$10$rAEP6TYRooG2MIR2C3z1Guy2FYXsNYr6ojnmSQYCTe36.M8lXOXMa', 'ADMIN', 'ACTIVE')
ON CONFLICT (username) DO NOTHING;

-- Инструктор
INSERT INTO users (id, version, created_at, updated_at, username, first_name, last_name, active, email, personnel_number, password_hash, role, status)
VALUES ('00000000-0000-0000-0000-000000000002', 0, now(), now(), 'instructor', 'Иван', 'Инструкторов', true, 'instructor@factory.com', '000002', '$2a$10$rAEP6TYRooG2MIR2C3z1Guy2FYXsNYr6ojnmSQYCTe36.M8lXOXMa', 'INSTRUCTOR', 'ACTIVE')
ON CONFLICT (username) DO NOTHING;

-- Менеджер
INSERT INTO users (id, version, created_at, updated_at, username, first_name, last_name, active, email, personnel_number, password_hash, role, status)
VALUES ('00000000-0000-0000-0000-000000000003', 0, now(), now(), 'manager', 'Петр', 'Менеджеров', true, 'manager@factory.com', '000003', '$2a$10$rAEP6TYRooG2MIR2C3z1Guy2FYXsNYr6ojnmSQYCTe36.M8lXOXMa', 'MANAGER', 'ACTIVE')
ON CONFLICT (username) DO NOTHING;

-- Сотрудник
INSERT INTO users (id, version, created_at, updated_at, username, first_name, last_name, active, email, personnel_number, password_hash, role, status)
VALUES ('00000000-0000-0000-0000-000000000004', 0, now(), now(), 'employee', 'Сергей', 'Сотрудников', true, 'employee@factory.com', '000004', '$2a$10$rAEP6TYRooG2MIR2C3z1Guy2FYXsNYr6ojnmSQYCTe36.M8lXOXMa', 'EMPLOYEE', 'ACTIVE')
ON CONFLICT (username) DO NOTHING;

-- Идемпотентный сидер для курсов, модулей и уроков

-- 1. Курс
INSERT INTO courses (id, version, created_at, updated_at, title, description, thumbnail_url, is_published, estimated_duration_minutes, author_id)
VALUES (
    'c0000000-0000-0000-0000-000000000001', 
    0, now(), now(), 
    'Основы промышленной безопасности', 
    'Базовый курс по технике безопасности и охране труда на промышленном предприятии.', 
    'https://images.unsplash.com/photo-1581092921461-7d1568637f6a', 
    true, 
    120, 
    '00000000-0000-0000-0000-000000000002'
) ON CONFLICT (id) DO NOTHING;

-- 2. Модули
INSERT INTO modules (id, version, created_at, updated_at, title, description, order_index, course_id)
VALUES (
    'b0000000-0000-0000-0000-000000000001', 
    0, now(), now(), 
    'Введение и нормативная база', 
    'Основные законы и правила внутреннего распорядка.', 
    1, 
    'c0000000-0000-0000-0000-000000000001'
) ON CONFLICT (id) DO NOTHING;

INSERT INTO modules (id, version, created_at, updated_at, title, description, order_index, course_id)
VALUES (
    'b0000000-0000-0000-0000-000000000002', 
    0, now(), now(), 
    'Средства индивидуальной защиты (СИЗ)', 
    'Виды СИЗ, правила выбора и использования.', 
    2, 
    'c0000000-0000-0000-0000-000000000001'
) ON CONFLICT (id) DO NOTHING;

-- 3. Уроки
-- Уроки для модуля 1
INSERT INTO lessons (id, version, created_at, updated_at, title, content, video_url, order_index, module_id)
VALUES (
    'e0000000-0000-0000-0000-000000000001', 
    0, now(), now(), 
    'Права и обязанности работника', 
    '{"text": "Описание прав и обязанностей..."}'::jsonb, 
    'https://example.com/video1', 
    1, 
    'b0000000-0000-0000-0000-000000000001'
) ON CONFLICT (id) DO NOTHING;

INSERT INTO lessons (id, version, created_at, updated_at, title, content, video_url, order_index, module_id)
VALUES (
    'e0000000-0000-0000-0000-000000000002', 
    0, now(), now(), 
    'Классификация опасных факторов', 
    '{"text": "Основные виды рисков..."}'::jsonb, 
    'https://example.com/video2', 
    2, 
    'b0000000-0000-0000-0000-000000000001'
) ON CONFLICT (id) DO NOTHING;

-- Уроки для модуля 2
INSERT INTO lessons (id, version, created_at, updated_at, title, content, video_url, order_index, module_id)
VALUES (
    'e0000000-0000-0000-0000-000000000003', 
    0, now(), now(), 
    'Защита головы и органов зрения', 
    '{"text": "Правила использования касок и очков..."}'::jsonb, 
    'https://example.com/video3', 
    1, 
    'b0000000-0000-0000-0000-000000000002'
) ON CONFLICT (id) DO NOTHING;

INSERT INTO lessons (id, version, created_at, updated_at, title, content, video_url, order_index, module_id)
VALUES (
    'e0000000-0000-0000-0000-000000000004', 
    0, now(), now(), 
    'Спецодежда и спецобувь', 
    '{"text": "Требования к защитной одежде..."}'::jsonb, 
    'https://example.com/video4', 
    2, 
    'b0000000-0000-0000-0000-000000000002'
) ON CONFLICT (id) DO NOTHING;
