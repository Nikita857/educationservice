package org.bm.service.education.learning.api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record LessonProgressResponse(
        UUID lessonId,
        String lessonTitle,
        boolean isCompleted,
        LocalDateTime completedAt) {
}
