package org.bm.service.education.learning.api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record CourseAccessRequestResponse(
        UUID id,
        UUID courseId,
        String courseTitle,
        UUID userId,
        String userFullName,
        UUID requestedById,
        String requestedByFullName,
        String status,
        String reason,
        LocalDateTime createdAt,
        LocalDateTime resolvedAt,
        String resolvedByFullName) {
}
