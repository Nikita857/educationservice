package org.bm.service.education.learning.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bm.service.education.common.api.ApiResponse;
import org.bm.service.education.common.api.PaginatedResponse;
import org.bm.service.education.identity.domain.User;
import org.bm.service.education.learning.api.dto.response.EnrollmentResponse;
import org.bm.service.education.learning.api.dto.response.LessonProgressResponse;
import org.bm.service.education.learning.application.EnrollmentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
@Tag(name = "Enrollments", description = "Запись на курсы и отслеживание прогресса")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    // === Записи на курсы ===

    @Operation(summary = "Записаться на курс")
    @PostMapping("courses/{courseId}/enroll")
    public ApiResponse<EnrollmentResponse> enrollInCourse(
            @Parameter(description = "UUID курса") @PathVariable UUID courseId,
            @AuthenticationPrincipal User currentUser) {
        EnrollmentResponse response = enrollmentService.enrollInCourse(courseId, currentUser);
        return ApiResponse.created(response, "/api/v1/courses/" + courseId + "/enroll");
    }

    @Operation(summary = "Моя запись на курс")
    @GetMapping("courses/{courseId}/enrollment")
    public ApiResponse<EnrollmentResponse> getMyEnrollment(
            @Parameter(description = "UUID курса") @PathVariable UUID courseId,
            @AuthenticationPrincipal User currentUser) {
        EnrollmentResponse response = enrollmentService.getMyEnrollment(courseId, currentUser);
        return ApiResponse.ok(response, "/api/v1/courses/" + courseId + "/enrollment");
    }

    @Operation(summary = "Мои записи на курсы")
    @GetMapping("enrollments")
    public PaginatedResponse<EnrollmentResponse> getMyEnrollments(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return enrollmentService.getMyEnrollments(currentUser, page, size, "/api/v1/enrollments");
    }

    @Operation(summary = "Отписаться от курса")
    @DeleteMapping("courses/{courseId}/enroll")
    public ApiResponse<Void> dropEnrollment(
            @Parameter(description = "UUID курса") @PathVariable UUID courseId,
            @AuthenticationPrincipal User currentUser) {
        enrollmentService.dropEnrollment(courseId, currentUser);
        return ApiResponse.ok(null, "/api/v1/courses/" + courseId + "/enroll");
    }

    // === Прогресс по урокам ===

    @Operation(summary = "Отметить урок как пройденный")
    @PostMapping("lessons/{lessonId}/complete")
    public ApiResponse<LessonProgressResponse> markLessonCompleted(
            @Parameter(description = "UUID урока") @PathVariable UUID lessonId,
            @AuthenticationPrincipal User currentUser) {
        LessonProgressResponse response = enrollmentService.markLessonCompleted(lessonId, currentUser);
        return ApiResponse.ok(response, "/api/v1/lessons/" + lessonId + "/complete");
    }

    @Operation(summary = "Мой прогресс по курсу")
    @GetMapping("courses/{courseId}/progress")
    public ApiResponse<List<LessonProgressResponse>> getMyProgress(
            @Parameter(description = "UUID курса") @PathVariable UUID courseId,
            @AuthenticationPrincipal User currentUser) {
        List<LessonProgressResponse> progress = enrollmentService.getLessonProgress(courseId, currentUser);
        return ApiResponse.ok(progress, "/api/v1/courses/" + courseId + "/progress");
    }
}
