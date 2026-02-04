package org.bm.service.education.learning.api.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateCourseRequest(
        @Size(max = 255, message = "Название должно быть не более 255 символов") String title,

        String description,

        String thumbnailUrl,

        Integer estimatedDurationMinutes) {
}
