package org.bm.service.education.learning.api.dto.response;

import java.util.UUID;

/**
 * Полный ответ варианта ответа (для инструктора, с пометкой isCorrect)
 */
public record AnswerOptionDetailResponse(
        UUID id,
        String text,
        int orderIndex,
        boolean isCorrect) {
}
