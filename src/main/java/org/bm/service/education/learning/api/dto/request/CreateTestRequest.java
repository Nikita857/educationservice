package org.bm.service.education.learning.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.bm.service.education.learning.domain.assessment.TestTargetType;

import java.util.UUID;

public record CreateTestRequest(
        @NotBlank(message = "Название теста обязательно") @Size(max = 255, message = "Название не более 255 символов") String title,

        String description,

        @Min(value = 0, message = "Проходной балл не может быть отрицательным") Integer passingScore,

        @Min(value = 1, message = "Лимит времени минимум 1 минута") Integer timeLimitMinutes,

        @Min(value = 0, message = "Количество попыток не может быть отрицательным") Integer maxAttempts,

        @NotNull(message = "Тип цели обязателен") TestTargetType targetType,

        @NotNull(message = "ID цели обязателен") UUID targetId) {
}
