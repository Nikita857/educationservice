package org.bm.service.education.learning.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateAnswerOptionRequest(
        @NotBlank(message = "Текст ответа обязателен") String text,

        boolean isCorrect) {
}
