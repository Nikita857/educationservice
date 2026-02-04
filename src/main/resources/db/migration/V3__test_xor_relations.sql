-- Добавление связей Test с Module и Course (XOR: только одна связь активна)

-- 1. Делаем lesson_id nullable
ALTER TABLE tests ALTER COLUMN lesson_id DROP NOT NULL;

-- 2. Добавляем колонки для module_id и course_id
ALTER TABLE tests ADD COLUMN module_id UUID;
ALTER TABLE tests ADD COLUMN course_id UUID;

-- 3. Добавляем FK constraints
ALTER TABLE tests ADD CONSTRAINT fk_tests_module FOREIGN KEY (module_id) REFERENCES modules(id);
ALTER TABLE tests ADD CONSTRAINT fk_tests_course FOREIGN KEY (course_id) REFERENCES courses(id);

-- 4. Добавляем индексы
CREATE INDEX idx_tests_module ON tests(module_id);
CREATE INDEX idx_tests_course ON tests(course_id);

-- 5. Добавляем XOR constraint (только одна связь должна быть заполнена)
ALTER TABLE tests ADD CONSTRAINT chk_tests_xor_target CHECK (
    (CASE WHEN lesson_id IS NOT NULL THEN 1 ELSE 0 END +
     CASE WHEN module_id IS NOT NULL THEN 1 ELSE 0 END +
     CASE WHEN course_id IS NOT NULL THEN 1 ELSE 0 END) = 1
);

-- 6. Добавляем unique constraints (только один тест на урок/модуль/курс)
ALTER TABLE tests ADD CONSTRAINT uq_tests_lesson UNIQUE (lesson_id);
ALTER TABLE tests ADD CONSTRAINT uq_tests_module UNIQUE (module_id);
ALTER TABLE tests ADD CONSTRAINT uq_tests_course UNIQUE (course_id);
