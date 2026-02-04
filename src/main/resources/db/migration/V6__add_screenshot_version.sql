-- Добавление колонки version для оптимистической блокировки
ALTER TABLE screenshot_submissions ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
