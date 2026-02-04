package org.bm.service.education.learning.api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record TestAttemptResponse(
        UUID id,
        UUID testId,
        String testTitle,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        Integer score,
        boolean isPassed,
        Integer timeLimitMinutes) {
}
