package org.bm.service.education.learning.api.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCourseRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must be less than 255 characters")
        String title,

        String description,

        String thumbnailUrl,

        Integer estimatedDurationMinutes) {
}
