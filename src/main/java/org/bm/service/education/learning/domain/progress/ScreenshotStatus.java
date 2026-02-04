package org.bm.service.education.learning.domain.progress;

/**
 * Статус проверки скриншота подтверждения
 */
public enum ScreenshotStatus {
    /** Ожидает проверки */
    PENDING,
    /** Одобрен */
    APPROVED,
    /** Отклонен */
    REJECTED
}
