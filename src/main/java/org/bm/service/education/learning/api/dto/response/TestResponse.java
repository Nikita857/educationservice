package org.bm.service.education.learning.api.dto.response;

import java.util.UUID;

public record TestResponse(
        UUID id,
        String title,
        String description,
        int passingScore,
        Integer timeLimitMinutes,
        int maxAttempts,
        int questionsCount,
        String targetType, // LESSON, MODULE, COURSE
        UUID targetId,
        String targetTitle) {
}
