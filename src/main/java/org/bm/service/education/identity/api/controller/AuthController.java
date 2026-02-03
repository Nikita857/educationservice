package org.bm.service.education.identity.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bm.service.education.common.api.ApiResponse;
import org.bm.service.education.identity.api.dto.request.LoginRequest;
import org.bm.service.education.identity.api.dto.request.RefreshTokenRequest;
import org.bm.service.education.identity.api.dto.response.AuthResponse;
import org.bm.service.education.identity.application.AuthService;
import org.bm.service.education.identity.domain.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ApiResponse.ok(response, "/api/v1/auth/login");
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refresh(request);
        return ApiResponse.ok(response, "/api/v1/auth/refresh");
    }

    @GetMapping("/me")
    public ApiResponse<AuthResponse> getCurrentUser(
            @AuthenticationPrincipal User user) {
        AuthResponse response = authService.getCurrentUser(user);
        return ApiResponse.ok(response, "/api/v1/auth/me");
    }
}
