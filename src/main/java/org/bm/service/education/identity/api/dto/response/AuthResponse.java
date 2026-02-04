package org.bm.service.education.identity.api.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AuthResponse(
                String accessToken,
                String refreshToken,
                String tokenType,
                UserInfo user) {
        @Builder
        public record UserInfo(
                        UUID id,
                        String username,
                        String firstName,
                        String lastName,
                        String email,
                        String role) {
        }

        public static AuthResponse of(String accessToken, String refreshToken, UserInfo user) {
                return AuthResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .tokenType("Bearer")
                                .user(user)
                                .build();
        }
}
