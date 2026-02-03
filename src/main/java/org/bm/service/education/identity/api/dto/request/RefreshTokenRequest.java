package org.bm.service.education.identity.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token is required") String refreshToken) {
}
