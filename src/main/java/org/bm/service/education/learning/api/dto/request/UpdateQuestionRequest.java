package org.bm.service.education.learning.api.dto.request;

import jakarta.validation.constraints.Min;

public record UpdateQuestionRequest(
        String text,

        @Min(value = 1, message = "Баллы за вопрос минимум 1") Integer points,

        Integer orderIndex) {
}
