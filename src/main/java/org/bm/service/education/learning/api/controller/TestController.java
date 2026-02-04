package org.bm.service.education.learning.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bm.service.education.common.api.ApiResponse;
import org.bm.service.education.identity.domain.User;
import org.bm.service.education.learning.api.dto.request.SubmitTestRequest;
import org.bm.service.education.learning.api.dto.response.QuestionResponse;
import org.bm.service.education.learning.api.dto.response.TestAttemptResponse;
import org.bm.service.education.learning.api.dto.response.TestResponse;
import org.bm.service.education.learning.application.TestService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
@Tag(name = "Tests", description = "Прохождение тестов")
public class TestController {

    private final TestService testService;

    // === Информация о тесте ===

    @Operation(summary = "Получить тест урока")
    @GetMapping("lessons/{lessonId}/test")
    public ApiResponse<TestResponse> getTestByLesson(
            @Parameter(description = "UUID урока") @PathVariable UUID lessonId) {
        TestResponse response = testService.getTestByLessonId(lessonId);
        return ApiResponse.ok(response, "/api/v1/lessons/" + lessonId + "/test");
    }

    @Operation(summary = "Получить тест модуля")
    @GetMapping("modules/{moduleId}/test")
    public ApiResponse<TestResponse> getTestByModule(
            @Parameter(description = "UUID модуля") @PathVariable UUID moduleId) {
        TestResponse response = testService.getTestByModuleId(moduleId);
        return ApiResponse.ok(response, "/api/v1/modules/" + moduleId + "/test");
    }

    @Operation(summary = "Получить тест курса")
    @GetMapping("courses/{courseId}/test")
    public ApiResponse<TestResponse> getTestByCourse(
            @Parameter(description = "UUID курса") @PathVariable UUID courseId) {
        TestResponse response = testService.getTestByCourseId(courseId);
        return ApiResponse.ok(response, "/api/v1/courses/" + courseId + "/test");
    }

    @Operation(summary = "Получить вопросы теста", description = "Возвращает список вопросов и вариантов ответов (без пометки правильных)")
    @GetMapping("tests/{testId}/questions")
    public ApiResponse<List<QuestionResponse>> getTestQuestions(
            @Parameter(description = "UUID теста") @PathVariable UUID testId) {
        List<QuestionResponse> questions = testService.getTestQuestions(testId);
        return ApiResponse.ok(questions, "/api/v1/tests/" + testId + "/questions");
    }

    // === Прохождение теста ===

    @Operation(summary = "Начать тест", description = "Создает новую попытку прохождения теста")
    @PostMapping("tests/{testId}/start")
    public ApiResponse<TestAttemptResponse> startTest(
            @Parameter(description = "UUID теста") @PathVariable UUID testId,
            @AuthenticationPrincipal User currentUser) {
        TestAttemptResponse response = testService.startTest(testId, currentUser);
        return ApiResponse.created(response, "/api/v1/tests/" + testId + "/start");
    }

    @Operation(summary = "Отправить ответы", description = "Завершает попытку и возвращает результат")
    @PostMapping("test-attempts/{attemptId}/submit")
    public ApiResponse<TestAttemptResponse> submitTest(
            @Parameter(description = "UUID попытки") @PathVariable UUID attemptId,
            @Valid @RequestBody SubmitTestRequest request,
            @AuthenticationPrincipal User currentUser) {
        TestAttemptResponse response = testService.submitTest(attemptId, request, currentUser);
        return ApiResponse.ok(response, "/api/v1/test-attempts/" + attemptId);
    }

    // === Результаты ===

    @Operation(summary = "Результат попытки")
    @GetMapping("test-attempts/{attemptId}")
    public ApiResponse<TestAttemptResponse> getAttemptResult(
            @Parameter(description = "UUID попытки") @PathVariable UUID attemptId,
            @AuthenticationPrincipal User currentUser) {
        TestAttemptResponse response = testService.getAttemptResult(attemptId, currentUser);
        return ApiResponse.ok(response, "/api/v1/test-attempts/" + attemptId);
    }

    @Operation(summary = "Мои попытки по тесту")
    @GetMapping("tests/{testId}/my-attempts")
    public ApiResponse<List<TestAttemptResponse>> getMyAttempts(
            @Parameter(description = "UUID теста") @PathVariable UUID testId,
            @AuthenticationPrincipal User currentUser) {
        List<TestAttemptResponse> attempts = testService.getMyAttempts(testId, currentUser);
        return ApiResponse.ok(attempts, "/api/v1/tests/" + testId + "/my-attempts");
    }
}
