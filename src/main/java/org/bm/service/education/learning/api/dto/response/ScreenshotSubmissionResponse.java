package org.bm.service.education.learning.api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ScreenshotSubmissionResponse(
        UUID id,
        UUID lessonProgressId,
        UUID lessonId,
        String lessonTitle,
        String studentName,
        String filePath,
        String status,
        String reviewComment,
        UUID reviewedById,
        String reviewedByName,
        LocalDateTime reviewedAt,
        LocalDateTime createdAt) {
}
