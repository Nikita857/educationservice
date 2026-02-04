package org.bm.service.education.identity.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.bm.service.education.common.api.ApiResponse;
import org.bm.service.education.identity.api.dto.request.LoginRequest;
import org.bm.service.education.identity.api.dto.request.RefreshTokenRequest;
import org.bm.service.education.identity.api.dto.request.RegisterRequest;
import org.bm.service.education.identity.api.dto.response.AuthResponse;
import org.bm.service.education.identity.application.AuthService;
import org.bm.service.education.identity.domain.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Управление аутентификацией и сессиями пользователей")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Регистрация нового пользователя", description = "Создает новый аккаунт сотрудника")
    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ApiResponse.ok(response, "/api/v1/auth/register");
    }

    @Operation(summary = "Вход в систему", description = "Возвращает пару access и refresh токенов")
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ApiResponse.ok(response, "/api/v1/auth/login");
    }

    @Operation(summary = "Обновление токена", description = "Использует refresh token для получения нового access токена")
    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refresh(request);
        return ApiResponse.ok(response, "/api/v1/auth/refresh");
    }

    @Operation(summary = "Получить текущего пользователя", description = "Возвращает данные профиля авторизованного пользователя")
    @GetMapping("/me")
    public ApiResponse<AuthResponse> getCurrentUser(
            @AuthenticationPrincipal User user) {
        AuthResponse response = authService.getCurrentUser(user);
        return ApiResponse.ok(response, "/api/v1/auth/me");
    }
}
