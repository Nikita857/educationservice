package org.bm.service.education.learning.api.dto.response;

import java.util.List;
import java.util.UUID;

/**
 * Полный ответ с вопросом и вариантами ответов (для инструктора, с пометкой
 * правильных)
 */
public record QuestionDetailResponse(
        UUID id,
        String text,
        int orderIndex,
        int points,
        List<AnswerOptionDetailResponse> options) {
}
