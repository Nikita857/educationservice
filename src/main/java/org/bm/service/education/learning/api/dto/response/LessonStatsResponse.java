package org.bm.service.education.learning.api.dto.response;

import java.util.UUID;

/**
 * Статистика по уроку (для выявления проблемных мест)
 */
public record LessonStatsResponse(
        UUID lessonId,
        String lessonTitle,
        String moduleName,
        String completionType,
        int totalAttempts,
        int completedCount,
        double completionRate,
        Double averageTestScore, // null если нет теста
        int pendingScreenshots // 0 если не SCREENSHOT
) {
}
