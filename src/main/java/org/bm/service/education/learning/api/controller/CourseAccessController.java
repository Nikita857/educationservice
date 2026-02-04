package org.bm.service.education.learning.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bm.service.education.common.api.ApiResponse;
import org.bm.service.education.common.api.PaginatedResponse;
import org.bm.service.education.identity.domain.User;
import org.bm.service.education.learning.api.dto.request.CreateCourseAccessRequest;
import org.bm.service.education.learning.api.dto.response.CourseAccessRequestResponse;
import org.bm.service.education.learning.api.dto.response.CourseAccessResponse;
import org.bm.service.education.learning.application.CourseAccessService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
@Tag(name = "Course Access", description = "Управление доступом к курсам")
public class CourseAccessController {

    private final CourseAccessService courseAccessService;

    // === Заявки на доступ ===

    @Operation(summary = "Создать заявку на доступ", description = "MANAGER создает заявку на доступ для своего сотрудника")
    @PostMapping("courses/{courseId}/access-requests")
    public ApiResponse<CourseAccessRequestResponse> createAccessRequest(
            @Parameter(description = "UUID курса") @PathVariable UUID courseId,
            @Valid @RequestBody CreateCourseAccessRequest request,
            @AuthenticationPrincipal User currentUser) {
        CourseAccessRequestResponse response = courseAccessService.createAccessRequest(courseId, request, currentUser);
        return ApiResponse.created(response, "/api/v1/courses/" + courseId + "/access-requests");
    }

    @Operation(summary = "Список заявок на курс", description = "INSTRUCTOR получает список заявок на доступ к курсу")
    @GetMapping("courses/{courseId}/access-requests")
    public PaginatedResponse<CourseAccessRequestResponse> getAccessRequests(
            @Parameter(description = "UUID курса") @PathVariable UUID courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return courseAccessService.getAccessRequests(courseId, page, size,
                "/api/v1/courses/" + courseId + "/access-requests");
    }

    @Operation(summary = "Одобрить заявку", description = "INSTRUCTOR одобряет заявку и выдает доступ пользователю")
    @PostMapping("access-requests/{requestId}/approve")
    public ApiResponse<CourseAccessRequestResponse> approveRequest(
            @Parameter(description = "UUID заявки") @PathVariable UUID requestId,
            @AuthenticationPrincipal User currentUser) {
        CourseAccessRequestResponse response = courseAccessService.approveRequest(requestId, currentUser);
        return ApiResponse.ok(response, "/api/v1/access-requests/" + requestId);
    }

    @Operation(summary = "Отклонить заявку", description = "INSTRUCTOR отклоняет заявку на доступ")
    @PostMapping("access-requests/{requestId}/reject")
    public ApiResponse<CourseAccessRequestResponse> rejectRequest(
            @Parameter(description = "UUID заявки") @PathVariable UUID requestId,
            @AuthenticationPrincipal User currentUser) {
        CourseAccessRequestResponse response = courseAccessService.rejectRequest(requestId, currentUser);
        return ApiResponse.ok(response, "/api/v1/access-requests/" + requestId);
    }

    // === Прямое управление доступом ===

    @Operation(summary = "Выдать прямой доступ", description = "INSTRUCTOR выдает доступ к курсу напрямую без заявки")
    @PostMapping("courses/{courseId}/access")
    public ApiResponse<CourseAccessResponse> grantDirectAccess(
            @Parameter(description = "UUID курса") @PathVariable UUID courseId,
            @Valid @RequestBody CreateCourseAccessRequest request,
            @AuthenticationPrincipal User currentUser) {
        CourseAccessResponse response = courseAccessService.grantDirectAccess(courseId, request.userId(), currentUser);
        return ApiResponse.created(response, "/api/v1/courses/" + courseId + "/access");
    }

    @Operation(summary = "Список пользователей с доступом", description = "INSTRUCTOR получает список пользователей, имеющих доступ к курсу")
    @GetMapping("courses/{courseId}/access")
    public PaginatedResponse<CourseAccessResponse> getCourseAccesses(
            @Parameter(description = "UUID курса") @PathVariable UUID courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return courseAccessService.getCourseAccesses(courseId, page, size, "/api/v1/courses/" + courseId + "/access");
    }

    @Operation(summary = "Проверить доступ", description = "Проверить, имеет ли пользователь доступ к курсу")
    @GetMapping("courses/{courseId}/access/check")
    public ApiResponse<Boolean> checkAccess(
            @Parameter(description = "UUID курса") @PathVariable UUID courseId,
            @AuthenticationPrincipal User currentUser) {
        boolean hasAccess = courseAccessService.hasAccess(courseId, currentUser.getId());
        return ApiResponse.ok(hasAccess, "/api/v1/courses/" + courseId + "/access/check");
    }
}
