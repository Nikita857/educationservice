package org.bm.service.education.learning.api.dto.response;

import java.util.UUID;

public record AnswerOptionResponse(
        UUID id,
        String text,
        int orderIndex) {
}
