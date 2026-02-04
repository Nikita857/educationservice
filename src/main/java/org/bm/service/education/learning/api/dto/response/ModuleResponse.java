package org.bm.service.education.learning.api.dto.response;

import java.util.UUID;

public record ModuleResponse(
        UUID id,
        String title,
        String description,
        int orderIndex,
        int lessonsCount) {
}
