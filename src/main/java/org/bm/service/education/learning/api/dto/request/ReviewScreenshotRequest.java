package org.bm.service.education.learning.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReviewScreenshotRequest(
        boolean approved,
        @NotBlank String comment) {
}
