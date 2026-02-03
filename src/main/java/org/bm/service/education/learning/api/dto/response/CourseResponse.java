package org.bm.service.education.learning.api.dto.response;

import java.util.UUID;

public record CourseResponse(
        UUID id,
        String title,
        String description,
        String thumbnailUrl,
        boolean isPublished,
        Integer estimatedDurationMinutes,
        UUID authorId,
        String authorName,
        int modulesCount) {
}
