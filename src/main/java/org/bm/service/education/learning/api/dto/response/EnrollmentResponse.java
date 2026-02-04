package org.bm.service.education.learning.api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record EnrollmentResponse(
        UUID id,
        UUID courseId,
        String courseTitle,
        UUID userId,
        String userFullName,
        String status,
        int progressPercent,
        LocalDateTime enrolledAt,
        LocalDateTime completedAt) {
}
