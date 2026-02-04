package org.bm.service.education.learning.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateQuestionRequest(
        @NotBlank(message = "Текст вопроса обязателен") String text,

        @Min(value = 1, message = "Баллы за вопрос минимум 1") Integer points,

        @NotEmpty(message = "Необходимо минимум 2 варианта ответа") @Valid List<CreateAnswerOptionRequest> options) {
}
