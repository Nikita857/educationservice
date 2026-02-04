package org.bm.service.education.learning.api.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record SubmitTestRequest(
        @NotEmpty(message = "Необходимо ответить хотя бы на один вопрос") Map<UUID, List<UUID>> answers) {
}
