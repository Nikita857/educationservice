package org.bm.service.education.learning.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bm.service.education.common.api.ApiResponse;
import org.bm.service.education.identity.domain.User;
import org.bm.service.education.learning.api.dto.request.ReviewScreenshotRequest;
import org.bm.service.education.learning.api.dto.response.ScreenshotSubmissionResponse;
import org.bm.service.education.learning.application.ScreenshotService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Screenshots", description = "Скриншоты подтверждения")
public class ScreenshotController {

    private final ScreenshotService screenshotService;

    // ==================== СТУДЕНТ ====================

    @Operation(summary = "Загрузить скриншот подтверждения", description = "Загружает скриншот для урока с типом закрытия SCREENSHOT")
    @PostMapping(value = "/lesson-progress/{lessonProgressId}/screenshots", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ScreenshotSubmissionResponse> submitScreenshot(
            @Parameter(description = "UUID прогресса урока") @PathVariable UUID lessonProgressId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User currentUser) {

        ScreenshotSubmissionResponse response = screenshotService.submitScreenshot(lessonProgressId, file, currentUser);
        return ApiResponse.created(response, "/api/v1/lesson-progress/" + lessonProgressId + "/screenshots");
    }

    @Operation(summary = "Получить мои скриншоты для урока")
    @GetMapping("/lesson-progress/{lessonProgressId}/screenshots")
    public ApiResponse<List<ScreenshotSubmissionResponse>> getMyScreenshots(
            @Parameter(description = "UUID прогресса урока") @PathVariable UUID lessonProgressId,
            @AuthenticationPrincipal User currentUser) {

        List<ScreenshotSubmissionResponse> screenshots = screenshotService.getMyScreenshots(lessonProgressId,
                currentUser);
        return ApiResponse.ok(screenshots, "/api/v1/lesson-progress/" + lessonProgressId + "/screenshots");
    }

    // ==================== ИНСТРУКТОР ====================

    @Operation(summary = "Получить скриншоты на проверку", description = "Возвращает все PENDING скриншоты для курса")
    @GetMapping("/admin/courses/{courseId}/screenshots/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ApiResponse<List<ScreenshotSubmissionResponse>> getPendingScreenshots(
            @Parameter(description = "UUID курса") @PathVariable UUID courseId,
            @AuthenticationPrincipal User currentUser) {

        List<ScreenshotSubmissionResponse> screenshots = screenshotService.getPendingScreenshots(courseId, currentUser);
        return ApiResponse.ok(screenshots, "/api/v1/admin/courses/" + courseId + "/screenshots/pending");
    }

    @Operation(summary = "Рецензировать скриншот", description = "Одобрить или отклонить скриншот. При одобрении урок автоматически отмечается как завершенный.")
    @PostMapping("/admin/screenshots/{submissionId}/review")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ApiResponse<ScreenshotSubmissionResponse> reviewScreenshot(
            @Parameter(description = "UUID скриншота") @PathVariable UUID submissionId,
            @Valid @RequestBody ReviewScreenshotRequest request,
            @AuthenticationPrincipal User currentUser) {

        ScreenshotSubmissionResponse response = screenshotService.reviewScreenshot(submissionId, request, currentUser);
        return ApiResponse.ok(response, "/api/v1/admin/screenshots/" + submissionId + "/review");
    }
}
