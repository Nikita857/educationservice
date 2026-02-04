package org.bm.service.education.learning.application;

import lombok.RequiredArgsConstructor;
import org.bm.service.education.common.exception.ResourceNotFoundException;
import org.bm.service.education.identity.domain.User;
import org.bm.service.education.learning.api.dto.request.SubmitTestRequest;
import org.bm.service.education.learning.api.dto.response.*;
import org.bm.service.education.learning.domain.assessment.*;
import org.bm.service.education.learning.infrastructure.TestAttemptRepository;
import org.bm.service.education.learning.infrastructure.TestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TestService {

    private final TestRepository testRepository;
    private final TestAttemptRepository attemptRepository;

    // === Получение информации о тесте ===

    public TestResponse getTestByLessonId(UUID lessonId) {
        Test test = testRepository.findByLessonId(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Тест для урока", lessonId));
        return toTestResponse(test);
    }

    public TestResponse getTestByModuleId(UUID moduleId) {
        Test test = testRepository.findByModuleId(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Тест для модуля", moduleId));
        return toTestResponse(test);
    }

    public TestResponse getTestByCourseId(UUID courseId) {
        Test test = testRepository.findByCourseId(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Тест для курса", courseId));
        return toTestResponse(test);
    }

    public List<QuestionResponse> getTestQuestions(UUID testId) {
        Test test = testRepository.findByIdWithQuestions(testId)
                .orElseThrow(() -> new ResourceNotFoundException("Тест", testId));
        return test.getQuestions().stream()
                .map(this::toQuestionResponse)
                .toList();
    }

    // === Начало и прохождение теста ===

    @Transactional
    public TestAttemptResponse startTest(UUID testId, User user) {
        Test test = findTestOrThrow(testId);

        // Проверяем лимит попыток
        if (test.getMaxAttempts() > 0) {
            long attempts = attemptRepository.countByUserIdAndTestId(user.getId(), testId);
            if (attempts >= test.getMaxAttempts()) {
                throw new IllegalStateException("Достигнут лимит попыток (" + test.getMaxAttempts() + ")");
            }
        }

        // Проверяем нет ли незавершенной попытки
        attemptRepository.findByUserIdAndTestIdAndFinishedAtIsNull(user.getId(), testId)
                .ifPresent(attempt -> {
                    throw new IllegalStateException("У вас уже есть незавершенная попытка");
                });

        TestAttempt attempt = TestAttempt.builder()
                .user(user)
                .test(test)
                .startedAt(LocalDateTime.now())
                .build();

        TestAttempt saved = attemptRepository.save(attempt);
        return toAttemptResponse(saved);
    }

    @Transactional
    public TestAttemptResponse submitTest(UUID attemptId, SubmitTestRequest request, User user) {
        TestAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Попытка теста", attemptId));

        if (!attempt.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Это не ваша попытка");
        }

        if (attempt.getFinishedAt() != null) {
            throw new IllegalStateException("Тест уже завершен");
        }

        Test test = testRepository.findByIdWithQuestions(attempt.getTest().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Тест", attempt.getTest().getId()));

        // Проверяем таймаут
        if (test.getTimeLimitMinutes() != null) {
            LocalDateTime deadline = attempt.getStartedAt().plusMinutes(test.getTimeLimitMinutes());
            if (LocalDateTime.now().isAfter(deadline)) {
                attempt.finish(0, test.getPassingScore());
                attemptRepository.save(attempt);
                throw new IllegalStateException("Время на прохождение теста истекло");
            }
        }

        // Подсчитываем баллы
        int totalPoints = 0;
        int earnedPoints = 0;

        for (Question question : test.getQuestions()) {
            totalPoints += question.getPoints();

            Set<UUID> correctAnswerIds = question.getAnswerOptions().stream()
                    .filter(AnswerOption::isCorrect)
                    .map(AnswerOption::getId)
                    .collect(Collectors.toSet());

            List<UUID> userAnswerIds = request.answers().getOrDefault(question.getId(), List.of());

            // Вопрос считается правильным если все правильные ответы выбраны и нет
            // неправильных
            Set<UUID> userAnswerSet = Set.copyOf(userAnswerIds);
            if (correctAnswerIds.equals(userAnswerSet)) {
                earnedPoints += question.getPoints();
            }
        }

        int score = totalPoints > 0 ? (earnedPoints * 100) / totalPoints : 0;
        attempt.finish(score, test.getPassingScore());
        attemptRepository.save(attempt);

        return toAttemptResponse(attempt);
    }

    public TestAttemptResponse getAttemptResult(UUID attemptId, User user) {
        TestAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Попытка теста", attemptId));

        if (!attempt.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Это не ваша попытка");
        }

        return toAttemptResponse(attempt);
    }

    public List<TestAttemptResponse> getMyAttempts(UUID testId, User user) {
        return attemptRepository.findByUserIdAndTestId(user.getId(), testId)
                .stream()
                .map(this::toAttemptResponse)
                .toList();
    }

    // === Private helpers ===

    private Test findTestOrThrow(UUID id) {
        return testRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Тест", id));
    }

    private UUID getTargetId(Test test) {
        if (test.getLesson() != null)
            return test.getLesson().getId();
        if (test.getModule() != null)
            return test.getModule().getId();
        if (test.getCourse() != null)
            return test.getCourse().getId();
        return null;
    }

    private TestResponse toTestResponse(Test test) {
        return new TestResponse(
                test.getId(),
                test.getTitle(),
                test.getDescription(),
                test.getPassingScore(),
                test.getTimeLimitMinutes(),
                test.getMaxAttempts(),
                test.getQuestions().size(),
                test.getTargetType().name(),
                getTargetId(test),
                test.getTargetTitle());
    }

    private QuestionResponse toQuestionResponse(Question q) {
        return new QuestionResponse(
                q.getId(),
                q.getText(),
                q.getOrderIndex(),
                q.getPoints(),
                q.getAnswerOptions().stream()
                        .map(this::toAnswerOptionResponse)
                        .toList());
    }

    private AnswerOptionResponse toAnswerOptionResponse(AnswerOption o) {
        return new AnswerOptionResponse(
                o.getId(),
                o.getText(),
                o.getOrderIndex());
    }

    private TestAttemptResponse toAttemptResponse(TestAttempt a) {
        return new TestAttemptResponse(
                a.getId(),
                a.getTest().getId(),
                a.getTest().getTitle(),
                a.getStartedAt(),
                a.getFinishedAt(),
                a.getScore(),
                a.isPassed(),
                a.getTest().getTimeLimitMinutes());
    }
}
