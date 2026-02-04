package org.bm.service.education.learning.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bm.service.education.common.api.ApiResponse;
import org.bm.service.education.identity.domain.User;
import org.bm.service.education.learning.api.dto.request.*;
import org.bm.service.education.learning.api.dto.response.*;
import org.bm.service.education.learning.application.TestManagementService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/")
@RequiredArgsConstructor
@Tag(name = "Test Management", description = "Управление тестами (для инструкторов)")
@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
public class AdminTestController {

    private final TestManagementService testManagementService;

    // ==================== ТЕСТЫ ====================

    @Operation(summary = "Создать тест", description = "Создает тест для урока, модуля или курса")
    @PostMapping("tests")
    public ApiResponse<TestResponse> createTest(
            @Valid @RequestBody CreateTestRequest request,
            @AuthenticationPrincipal User currentUser) {
        TestResponse response = testManagementService.createTest(request, currentUser);
        return ApiResponse.created(response, "/api/v1/admin/tests");
    }

    @Operation(summary = "Обновить тест")
    @PutMapping("tests/{testId}")
    public ApiResponse<TestResponse> updateTest(
            @Parameter(description = "UUID теста") @PathVariable UUID testId,
            @Valid @RequestBody UpdateTestRequest request,
            @AuthenticationPrincipal User currentUser) {
        TestResponse response = testManagementService.updateTest(testId, request, currentUser);
        return ApiResponse.ok(response, "/api/v1/admin/tests/" + testId);
    }

    @Operation(summary = "Удалить тест")
    @DeleteMapping("tests/{testId}")
    public ApiResponse<Void> deleteTest(
            @Parameter(description = "UUID теста") @PathVariable UUID testId,
            @AuthenticationPrincipal User currentUser) {
        testManagementService.deleteTest(testId, currentUser);
        return ApiResponse.ok(null, "/api/v1/admin/tests/" + testId);
    }

    @Operation(summary = "Получить вопросы теста (с правильными ответами)")
    @GetMapping("tests/{testId}/questions")
    public ApiResponse<List<QuestionDetailResponse>> getTestQuestions(
            @Parameter(description = "UUID теста") @PathVariable UUID testId,
            @AuthenticationPrincipal User currentUser) {
        List<QuestionDetailResponse> questions = testManagementService.getTestQuestionsForAdmin(testId, currentUser);
        return ApiResponse.ok(questions, "/api/v1/admin/tests/" + testId + "/questions");
    }

    // ==================== ВОПРОСЫ ====================

    @Operation(summary = "Создать вопрос", description = "Создает вопрос с вариантами ответов")
    @PostMapping("tests/{testId}/questions")
    public ApiResponse<QuestionDetailResponse> createQuestion(
            @Parameter(description = "UUID теста") @PathVariable UUID testId,
            @Valid @RequestBody CreateQuestionRequest request,
            @AuthenticationPrincipal User currentUser) {
        QuestionDetailResponse response = testManagementService.createQuestion(testId, request, currentUser);
        return ApiResponse.created(response, "/api/v1/admin/tests/" + testId + "/questions");
    }

    @Operation(summary = "Обновить вопрос")
    @PutMapping("questions/{questionId}")
    public ApiResponse<QuestionDetailResponse> updateQuestion(
            @Parameter(description = "UUID вопроса") @PathVariable UUID questionId,
            @Valid @RequestBody UpdateQuestionRequest request,
            @AuthenticationPrincipal User currentUser) {
        QuestionDetailResponse response = testManagementService.updateQuestion(questionId, request, currentUser);
        return ApiResponse.ok(response, "/api/v1/admin/questions/" + questionId);
    }

    @Operation(summary = "Удалить вопрос")
    @DeleteMapping("questions/{questionId}")
    public ApiResponse<Void> deleteQuestion(
            @Parameter(description = "UUID вопроса") @PathVariable UUID questionId,
            @AuthenticationPrincipal User currentUser) {
        testManagementService.deleteQuestion(questionId, currentUser);
        return ApiResponse.ok(null, "/api/v1/admin/questions/" + questionId);
    }

    // ==================== ВАРИАНТЫ ОТВЕТОВ ====================

    @Operation(summary = "Добавить вариант ответа")
    @PostMapping("questions/{questionId}/options")
    public ApiResponse<AnswerOptionDetailResponse> addAnswerOption(
            @Parameter(description = "UUID вопроса") @PathVariable UUID questionId,
            @Valid @RequestBody CreateAnswerOptionRequest request,
            @AuthenticationPrincipal User currentUser) {
        AnswerOptionDetailResponse response = testManagementService.addAnswerOption(questionId, request, currentUser);
        return ApiResponse.created(response, "/api/v1/admin/questions/" + questionId + "/options");
    }

    @Operation(summary = "Обновить вариант ответа")
    @PutMapping("options/{optionId}")
    public ApiResponse<AnswerOptionDetailResponse> updateAnswerOption(
            @Parameter(description = "UUID варианта ответа") @PathVariable UUID optionId,
            @Valid @RequestBody UpdateAnswerOptionRequest request,
            @AuthenticationPrincipal User currentUser) {
        AnswerOptionDetailResponse response = testManagementService.updateAnswerOption(optionId, request, currentUser);
        return ApiResponse.ok(response, "/api/v1/admin/options/" + optionId);
    }

    @Operation(summary = "Удалить вариант ответа")
    @DeleteMapping("options/{optionId}")
    public ApiResponse<Void> deleteAnswerOption(
            @Parameter(description = "UUID варианта ответа") @PathVariable UUID optionId,
            @AuthenticationPrincipal User currentUser) {
        testManagementService.deleteAnswerOption(optionId, currentUser);
        return ApiResponse.ok(null, "/api/v1/admin/options/" + optionId);
    }
}
