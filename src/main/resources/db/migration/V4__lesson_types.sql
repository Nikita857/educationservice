-- Добавление типа урока и способа закрытия

ALTER TABLE lessons ADD COLUMN lesson_type VARCHAR(50) NOT NULL DEFAULT 'LECTURE';
ALTER TABLE lessons ADD COLUMN completion_type VARCHAR(50) NOT NULL DEFAULT 'READ';

-- Внешняя ссылка для уроков типа EXTERNAL
ALTER TABLE lessons ADD COLUMN external_url VARCHAR(500);

-- Ссылка на скриншот-подтверждение (для completion_type = SCREENSHOT)
-- Будет храниться в lesson_progress
