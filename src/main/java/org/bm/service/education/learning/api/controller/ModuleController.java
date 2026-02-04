package org.bm.service.education.learning.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bm.service.education.common.api.ApiResponse;
import org.bm.service.education.identity.domain.User;
import org.bm.service.education.learning.api.dto.request.CreateModuleRequest;
import org.bm.service.education.learning.api.dto.request.UpdateModuleRequest;
import org.bm.service.education.learning.api.dto.response.ModuleResponse;
import org.bm.service.education.learning.application.ModuleService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
@Tag(name = "Modules", description = "Управление модулями курса")
public class ModuleController {

    private final ModuleService moduleService;

    @Operation(summary = "Список модулей курса")
    @GetMapping("courses/{courseId}/modules")
    public ApiResponse<List<ModuleResponse>> getModulesByCourse(
            @Parameter(description = "UUID курса") @PathVariable UUID courseId) {
        List<ModuleResponse> modules = moduleService.getModulesByCourse(courseId);
        return ApiResponse.ok(modules, "/api/v1/courses/" + courseId + "/modules");
    }

    @Operation(summary = "Создать модуль в курсе")
    @PostMapping("courses/{courseId}/modules")
    public ApiResponse<ModuleResponse> createModule(
            @Parameter(description = "UUID курса") @PathVariable UUID courseId,
            @Valid @RequestBody CreateModuleRequest request,
            @AuthenticationPrincipal User currentUser) {
        ModuleResponse response = moduleService.createModule(courseId, request, currentUser);
        return ApiResponse.created(response, "/api/v1/courses/" + courseId + "/modules");
    }

    @Operation(summary = "Получить модуль по ID")
    @GetMapping("modules/{id}")
    public ApiResponse<ModuleResponse> getModuleById(
            @Parameter(description = "UUID модуля") @PathVariable UUID id) {
        ModuleResponse response = moduleService.getModuleById(id);
        return ApiResponse.ok(response, "/api/v1/modules/" + id);
    }

    @Operation(summary = "Обновить модуль")
    @PutMapping("modules/{id}")
    public ApiResponse<ModuleResponse> updateModule(
            @Parameter(description = "UUID модуля") @PathVariable UUID id,
            @Valid @RequestBody UpdateModuleRequest request,
            @AuthenticationPrincipal User currentUser) {
        ModuleResponse response = moduleService.updateModule(id, request, currentUser);
        return ApiResponse.ok(response, "/api/v1/modules/" + id);
    }

    @Operation(summary = "Удалить модуль")
    @DeleteMapping("modules/{id}")
    public ApiResponse<Void> deleteModule(
            @Parameter(description = "UUID модуля") @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        moduleService.deleteModule(id, currentUser);
        return ApiResponse.ok(null, "/api/v1/modules/" + id);
    }
}
