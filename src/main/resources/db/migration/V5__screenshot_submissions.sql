-- Скриншоты подтверждения для уроков с completionType = SCREENSHOT
CREATE TABLE screenshot_submissions (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    lesson_progress_id UUID NOT NULL REFERENCES lesson_progress(id) ON DELETE CASCADE,
    file_path VARCHAR(500) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',

    reviewed_by UUID REFERENCES users(id),
    reviewed_at TIMESTAMP,
    review_comment TEXT,

    CONSTRAINT chk_screenshot_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))
);

CREATE INDEX idx_screenshot_lesson_progress ON screenshot_submissions(lesson_progress_id);
CREATE INDEX idx_screenshot_status ON screenshot_submissions(status);
