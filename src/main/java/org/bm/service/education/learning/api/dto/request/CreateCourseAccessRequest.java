package org.bm.service.education.learning.api.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateCourseAccessRequest(
                @NotNull(message = "ID пользователя обязателен") UUID userId,

                String reason) {
}
