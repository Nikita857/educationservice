package org.bm.service.education.learning.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bm.service.education.common.api.ApiResponse;
import org.bm.service.education.identity.domain.User;
import org.bm.service.education.learning.api.dto.response.*;
import org.bm.service.education.learning.application.StatsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/stats")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "Статистика и дашборды для инструкторов")
@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
public class StatsController {

    private final StatsService statsService;

    @Operation(summary = "Статистика курса", description = "Общая статистика курса: студенты, прогресс, тесты, скриншоты")
    @GetMapping("/courses/{courseId}")
    public ApiResponse<CourseStatsResponse> getCourseStats(
            @Parameter(description = "UUID курса") @PathVariable UUID courseId,
            @AuthenticationPrincipal User currentUser) {

        CourseStatsResponse stats = statsService.getCourseStats(courseId, currentUser);
        return ApiResponse.ok(stats, "/api/v1/admin/stats/courses/" + courseId);
    }

    @Operation(summary = "Прогресс студентов", description = "Список всех студентов курса с их прогрессом")
    @GetMapping("/courses/{courseId}/students")
    public ApiResponse<List<StudentProgressResponse>> getStudentsProgress(
            @Parameter(description = "UUID курса") @PathVariable UUID courseId,
            @AuthenticationPrincipal User currentUser) {

        List<StudentProgressResponse> students = statsService.getStudentsProgress(courseId, currentUser);
        return ApiResponse.ok(students, "/api/v1/admin/stats/courses/" + courseId + "/students");
    }

    @Operation(summary = "Детальный прогресс студента", description = "Подробный прогресс конкретного студента по всем урокам")
    @GetMapping("/enrollments/{enrollmentId}")
    public ApiResponse<StudentProgressResponse> getStudentProgressDetail(
            @Parameter(description = "UUID записи на курс") @PathVariable UUID enrollmentId,
            @AuthenticationPrincipal User currentUser) {

        StudentProgressResponse progress = statsService.getStudentProgressDetail(enrollmentId, currentUser);
        return ApiResponse.ok(progress, "/api/v1/admin/stats/enrollments/" + enrollmentId);
    }
}
