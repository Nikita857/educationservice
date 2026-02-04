package org.bm.service.education.learning.api.dto.response;

import java.util.UUID;

public record LessonResponse(
        UUID id,
        String title,
        String content,
        String videoUrl,
        String externalUrl,
        String lessonType,
        String completionType,
        int orderIndex,
        boolean hasTest) {
}
