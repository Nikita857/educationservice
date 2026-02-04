package org.bm.service.education.learning.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bm.service.education.common.api.ApiResponse;
import org.bm.service.education.common.api.PaginatedResponse;
import org.bm.service.education.identity.domain.User;
import org.bm.service.education.learning.api.dto.request.CreateCourseRequest;
import org.bm.service.education.learning.api.dto.request.UpdateCourseRequest;
import org.bm.service.education.learning.api.dto.response.CourseResponse;
import org.bm.service.education.learning.application.CourseService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Управление учебными курсами")
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "Список опубликованных курсов", description = "Получить страницу доступных курсов с пагинацией")
    @GetMapping
    public PaginatedResponse<CourseResponse> getPublishedCourses(
            @Parameter(description = "Номер страницы (начиная с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size) {
        return courseService.getPublishedCourses(page, size, "/api/v1/courses");
    }

    @Operation(summary = "Получить курс по ID")
    @GetMapping("/{id}")
    public ApiResponse<CourseResponse> getCourseById(
            @Parameter(description = "UUID курса") @PathVariable UUID id) {
        CourseResponse response = courseService.getCourseById(id);
        return ApiResponse.ok(response, "/api/v1/courses/" + id);
    }

    @Operation(summary = "Создать новый курс", description = "Автором становится текущий пользователь")
    @PostMapping
    public ApiResponse<CourseResponse> createCourse(
            @Valid @RequestBody CreateCourseRequest request,
            @AuthenticationPrincipal User currentUser) {
        CourseResponse response = courseService.createCourse(request, currentUser);
        return ApiResponse.created(response, "/api/v1/courses");
    }

    @Operation(summary = "Обновить курс", description = "Доступно автору курса или администратору")
    @PutMapping("/{id}")
    public ApiResponse<CourseResponse> updateCourse(
            @Parameter(description = "UUID курса") @PathVariable UUID id,
            @Valid @RequestBody UpdateCourseRequest request,
            @AuthenticationPrincipal User currentUser) {
        CourseResponse response = courseService.updateCourse(id, request, currentUser);
        return ApiResponse.ok(response, "/api/v1/courses/" + id);
    }

    @Operation(summary = "Удалить курс", description = "Доступно автору курса или администратору")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCourse(
            @Parameter(description = "UUID курса") @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        courseService.deleteCourse(id, currentUser);
        return ApiResponse.ok(null, "/api/v1/courses/" + id);
    }

    @Operation(summary = "Опубликовать курс", description = "Делает курс видимым для всех пользователей")
    @PostMapping("/{id}/publish")
    public ApiResponse<CourseResponse> publishCourse(
            @Parameter(description = "UUID курса") @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        CourseResponse response = courseService.publishCourse(id, currentUser);
        return ApiResponse.ok(response, "/api/v1/courses/" + id);
    }

    @Operation(summary = "Снять курс с публикации", description = "Скрывает курс от общего списка")
    @PostMapping("/{id}/unpublish")
    public ApiResponse<CourseResponse> unpublishCourse(
            @Parameter(description = "UUID курса") @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        CourseResponse response = courseService.unpublishCourse(id, currentUser);
        return ApiResponse.ok(response, "/api/v1/courses/" + id);
    }
}
