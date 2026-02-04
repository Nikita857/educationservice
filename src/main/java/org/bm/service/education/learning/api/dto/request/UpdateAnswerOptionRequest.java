package org.bm.service.education.learning.api.dto.request;

public record UpdateAnswerOptionRequest(
        String text,
        Boolean isCorrect,
        Integer orderIndex) {
}
