package org.bm.service.education.learning.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bm.service.education.common.api.ApiResponse;
import org.bm.service.education.identity.domain.User;
import org.bm.service.education.learning.api.dto.request.CreateLessonRequest;
import org.bm.service.education.learning.api.dto.request.UpdateLessonRequest;
import org.bm.service.education.learning.api.dto.response.LessonResponse;
import org.bm.service.education.learning.application.LessonService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
@Tag(name = "Lessons", description = "Управление уроками модуля")
public class LessonController {

    private final LessonService lessonService;

    @Operation(summary = "Список уроков модуля")
    @GetMapping("modules/{moduleId}/lessons")
    public ApiResponse<List<LessonResponse>> getLessonsByModule(
            @Parameter(description = "UUID модуля") @PathVariable UUID moduleId) {
        List<LessonResponse> lessons = lessonService.getLessonsByModule(moduleId);
        return ApiResponse.ok(lessons, "/api/v1/modules/" + moduleId + "/lessons");
    }

    @Operation(summary = "Создать урок в модуле")
    @PostMapping("modules/{moduleId}/lessons")
    public ApiResponse<LessonResponse> createLesson(
            @Parameter(description = "UUID модуля") @PathVariable UUID moduleId,
            @Valid @RequestBody CreateLessonRequest request,
            @AuthenticationPrincipal User currentUser) {
        LessonResponse response = lessonService.createLesson(moduleId, request, currentUser);
        return ApiResponse.created(response, "/api/v1/modules/" + moduleId + "/lessons");
    }

    @Operation(summary = "Получить урок по ID")
    @GetMapping("lessons/{id}")
    public ApiResponse<LessonResponse> getLessonById(
            @Parameter(description = "UUID урока") @PathVariable UUID id) {
        LessonResponse response = lessonService.getLessonById(id);
        return ApiResponse.ok(response, "/api/v1/lessons/" + id);
    }

    @Operation(summary = "Обновить урок")
    @PutMapping("lessons/{id}")
    public ApiResponse<LessonResponse> updateLesson(
            @Parameter(description = "UUID урока") @PathVariable UUID id,
            @Valid @RequestBody UpdateLessonRequest request,
            @AuthenticationPrincipal User currentUser) {
        LessonResponse response = lessonService.updateLesson(id, request, currentUser);
        return ApiResponse.ok(response, "/api/v1/lessons/" + id);
    }

    @Operation(summary = "Удалить урок")
    @DeleteMapping("lessons/{id}")
    public ApiResponse<Void> deleteLesson(
            @Parameter(description = "UUID урока") @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        lessonService.deleteLesson(id, currentUser);
        return ApiResponse.ok(null, "/api/v1/lessons/" + id);
    }
}
