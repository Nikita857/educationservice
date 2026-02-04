package org.bm.service.education.learning.api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Прогресс студента по конкретному уроку
 */
public record StudentLessonProgress(
        UUID lessonId,
        String lessonTitle,
        String moduleName,
        String completionType,
        boolean isCompleted,
        LocalDateTime completedAt,

        // Для тестов
        Integer testScore,
        Boolean testPassed,

        // Для скриншотов
        String screenshotStatus) {
}
