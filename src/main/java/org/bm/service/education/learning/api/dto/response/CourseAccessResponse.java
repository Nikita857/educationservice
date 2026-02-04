package org.bm.service.education.learning.api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record CourseAccessResponse(
        UUID id,
        UUID courseId,
        String courseTitle,
        UUID userId,
        String userFullName,
        UUID grantedById,
        String grantedByFullName,
        LocalDateTime grantedAt,
        LocalDateTime expiresAt,
        boolean isActive) {
}
