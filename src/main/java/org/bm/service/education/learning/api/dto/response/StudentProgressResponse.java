package org.bm.service.education.learning.api.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Детальный прогресс студента по курсу
 */
public record StudentProgressResponse(
        UUID enrollmentId,
        UUID userId,
        String studentName,
        String email,
        String status,
        int progressPercent,
        LocalDateTime enrolledAt,
        LocalDateTime lastActivityAt,

        // Детализация по урокам
        int completedLessons,
        int totalLessons,
        List<StudentLessonProgress> lessons,

        // Тесты
        int passedTests,
        int totalTests,
        Double averageTestScore) {
}
