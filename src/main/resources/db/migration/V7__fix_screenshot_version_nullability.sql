-- Приведение колонки version к общему стилю проекта (nullable)
ALTER TABLE screenshot_submissions ALTER COLUMN version DROP NOT NULL;
ALTER TABLE screenshot_submissions ALTER COLUMN version SET DEFAULT NULL;
