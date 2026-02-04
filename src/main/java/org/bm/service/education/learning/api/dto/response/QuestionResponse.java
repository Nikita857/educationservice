package org.bm.service.education.learning.api.dto.response;

import java.util.List;
import java.util.UUID;

public record QuestionResponse(
        UUID id,
        String text,
        int orderIndex,
        int points,
        List<AnswerOptionResponse> options) {
}
