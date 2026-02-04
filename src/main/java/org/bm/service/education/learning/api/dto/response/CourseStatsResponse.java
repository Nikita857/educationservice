package org.bm.service.education.learning.api.dto.response;

import java.util.List;

/**
 * Общая статистика курса для инструктора
 */
public record CourseStatsResponse(
        // Общие метрики
        int totalStudents,
        int activeStudents,
        int completedStudents,
        int droppedStudents,

        // Прогресс
        double averageProgressPercent,

        // Уроки
        int totalLessons,
        List<LessonStatsResponse> lessonStats,

        // Тесты
        int totalTests,
        double averageTestScore,
        int totalTestAttempts,
        int passedTestAttempts,
        double testPassRate,

        // Скриншоты на проверку
        int pendingScreenshots) {
}
