package org.bm.service.education.learning.application;

import lombok.RequiredArgsConstructor;
import org.bm.service.education.common.exception.ResourceNotFoundException;
import org.bm.service.education.identity.domain.User;
import org.bm.service.education.identity.domain.UserRole;
import org.bm.service.education.learning.api.dto.request.*;
import org.bm.service.education.learning.api.dto.response.*;
import org.bm.service.education.learning.domain.Course;
import org.bm.service.education.learning.domain.Lesson;
import org.bm.service.education.learning.domain.Module;
import org.bm.service.education.learning.domain.assessment.*;
import org.bm.service.education.learning.infrastructure.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TestManagementService {

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;

    // ==================== ТЕСТЫ ====================

    @Transactional
    public TestResponse createTest(CreateTestRequest request, User currentUser) {
        // Проверяем что тест еще не существует для этой цели
        checkTestNotExists(request.targetType(), request.targetId());

        // Получаем курс для проверки прав
        Course course = getCourseForTarget(request.targetType(), request.targetId());
        checkEditPermission(course, currentUser);

        Test test = Test.builder()
                .title(request.title())
                .description(request.description())
                .passingScore(request.passingScore() != null ? request.passingScore() : 70)
                .timeLimitMinutes(request.timeLimitMinutes())
                .maxAttempts(request.maxAttempts() != null ? request.maxAttempts() : 0)
                .build();

        // Устанавливаем связь с целью
        setTestTarget(test, request.targetType(), request.targetId());

        Test saved = testRepository.save(test);
        return toTestResponse(saved);
    }

    @Transactional
    public TestResponse updateTest(UUID testId, UpdateTestRequest request, User currentUser) {
        Test test = findTestOrThrow(testId);
        checkEditPermissionForTest(test, currentUser);

        if (request.title() != null) {
            test.setTitle(request.title());
        }
        if (request.description() != null) {
            test.setDescription(request.description());
        }
        if (request.passingScore() != null) {
            test.setPassingScore(request.passingScore());
        }
        if (request.timeLimitMinutes() != null) {
            test.setTimeLimitMinutes(request.timeLimitMinutes());
        }
        if (request.maxAttempts() != null) {
            test.setMaxAttempts(request.maxAttempts());
        }

        Test saved = testRepository.save(test);
        return toTestResponse(saved);
    }

    @Transactional
    public void deleteTest(UUID testId, User currentUser) {
        Test test = findTestOrThrow(testId);
        checkEditPermissionForTest(test, currentUser);
        testRepository.delete(test);
    }

    public List<QuestionDetailResponse> getTestQuestionsForAdmin(UUID testId, User currentUser) {
        Test test = testRepository.findByIdWithQuestions(testId)
                .orElseThrow(() -> new ResourceNotFoundException("Тест", testId));
        checkEditPermissionForTest(test, currentUser);

        return test.getQuestions().stream()
                .map(this::toQuestionDetailResponse)
                .toList();
    }

    // ==================== ВОПРОСЫ ====================

    @Transactional
    public QuestionDetailResponse createQuestion(UUID testId, CreateQuestionRequest request, User currentUser) {
        Test test = findTestOrThrow(testId);
        checkEditPermissionForTest(test, currentUser);

        // Валидация: минимум один правильный ответ
        boolean hasCorrect = request.options().stream().anyMatch(CreateAnswerOptionRequest::isCorrect);
        if (!hasCorrect) {
            throw new IllegalArgumentException("Необходим минимум один правильный вариант ответа");
        }

        int nextOrder = questionRepository.findMaxOrderIndexByTestId(testId) + 1;

        Question question = Question.builder()
                .text(request.text())
                .points(request.points() != null ? request.points() : 1)
                .orderIndex(nextOrder)
                .test(test)
                .build();

        // Добавляем варианты ответов
        int optionOrder = 0;
        for (CreateAnswerOptionRequest optionRequest : request.options()) {
            AnswerOption option = AnswerOption.builder()
                    .text(optionRequest.text())
                    .isCorrect(optionRequest.isCorrect())
                    .orderIndex(optionOrder++)
                    .build();
            question.addAnswerOption(option);
        }

        Question saved = questionRepository.save(question);
        return toQuestionDetailResponse(saved);
    }

    @Transactional
    public QuestionDetailResponse updateQuestion(UUID questionId, UpdateQuestionRequest request, User currentUser) {
        Question question = questionRepository.findByIdWithOptions(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Вопрос", questionId));
        checkEditPermissionForTest(question.getTest(), currentUser);

        if (request.text() != null) {
            question.setText(request.text());
        }
        if (request.points() != null) {
            question.setPoints(request.points());
        }
        if (request.orderIndex() != null) {
            question.setOrderIndex(request.orderIndex());
        }

        Question saved = questionRepository.save(question);
        return toQuestionDetailResponse(saved);
    }

    @Transactional
    public void deleteQuestion(UUID questionId, User currentUser) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Вопрос", questionId));
        checkEditPermissionForTest(question.getTest(), currentUser);
        questionRepository.delete(question);
    }

    // ==================== ВАРИАНТЫ ОТВЕТОВ ====================

    @Transactional
    public AnswerOptionDetailResponse addAnswerOption(UUID questionId, CreateAnswerOptionRequest request,
            User currentUser) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Вопрос", questionId));
        checkEditPermissionForTest(question.getTest(), currentUser);

        int nextOrder = answerOptionRepository.findMaxOrderIndexByQuestionId(questionId) + 1;

        AnswerOption option = AnswerOption.builder()
                .text(request.text())
                .isCorrect(request.isCorrect())
                .orderIndex(nextOrder)
                .build();
        question.addAnswerOption(option);

        questionRepository.save(question);
        return toAnswerOptionDetailResponse(option);
    }

    @Transactional
    public AnswerOptionDetailResponse updateAnswerOption(UUID optionId, UpdateAnswerOptionRequest request,
            User currentUser) {
        AnswerOption option = answerOptionRepository.findById(optionId)
                .orElseThrow(() -> new ResourceNotFoundException("Вариант ответа", optionId));
        checkEditPermissionForTest(option.getQuestion().getTest(), currentUser);

        if (request.text() != null) {
            option.setText(request.text());
        }
        if (request.isCorrect() != null) {
            option.setCorrect(request.isCorrect());
        }
        if (request.orderIndex() != null) {
            option.setOrderIndex(request.orderIndex());
        }

        AnswerOption saved = answerOptionRepository.save(option);
        return toAnswerOptionDetailResponse(saved);
    }

    @Transactional
    public void deleteAnswerOption(UUID optionId, User currentUser) {
        AnswerOption option = answerOptionRepository.findById(optionId)
                .orElseThrow(() -> new ResourceNotFoundException("Вариант ответа", optionId));
        checkEditPermissionForTest(option.getQuestion().getTest(), currentUser);

        // Проверяем что останется хотя бы 2 варианта
        Question question = option.getQuestion();
        if (question.getAnswerOptions().size() <= 2) {
            throw new IllegalStateException("Необходимо минимум 2 варианта ответа");
        }

        answerOptionRepository.delete(option);
    }

    // ==================== PRIVATE HELPERS ====================

    private Test findTestOrThrow(UUID id) {
        return testRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Тест", id));
    }

    private void checkTestNotExists(TestTargetType targetType, UUID targetId) {
        boolean exists = switch (targetType) {
            case LESSON -> testRepository.existsByLessonId(targetId);
            case MODULE -> testRepository.existsByModuleId(targetId);
            case COURSE -> testRepository.existsByCourseId(targetId);
        };
        if (exists) {
            throw new IllegalStateException("Тест для этой цели уже существует");
        }
    }

    private Course getCourseForTarget(TestTargetType targetType, UUID targetId) {
        return switch (targetType) {
            case LESSON -> {
                Lesson lesson = lessonRepository.findById(targetId)
                        .orElseThrow(() -> new ResourceNotFoundException("Урок", targetId));
                yield lesson.getModule().getCourse();
            }
            case MODULE -> {
                Module module = moduleRepository.findById(targetId)
                        .orElseThrow(() -> new ResourceNotFoundException("Модуль", targetId));
                yield module.getCourse();
            }
            case COURSE -> courseRepository.findById(targetId)
                    .orElseThrow(() -> new ResourceNotFoundException("Курс", targetId));
        };
    }

    private void setTestTarget(Test test, TestTargetType targetType, UUID targetId) {
        switch (targetType) {
            case LESSON -> {
                Lesson lesson = lessonRepository.findById(targetId)
                        .orElseThrow(() -> new ResourceNotFoundException("Урок", targetId));
                test.setLesson(lesson);
            }
            case MODULE -> {
                Module module = moduleRepository.findById(targetId)
                        .orElseThrow(() -> new ResourceNotFoundException("Модуль", targetId));
                test.setModule(module);
            }
            case COURSE -> {
                Course course = courseRepository.findById(targetId)
                        .orElseThrow(() -> new ResourceNotFoundException("Курс", targetId));
                test.setCourse(course);
            }
        }
    }

    private void checkEditPermission(Course course, User currentUser) {
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
        boolean isInstructor = currentUser.getRole() == UserRole.INSTRUCTOR;
        boolean isAuthor = course.getAuthor() != null && course.getAuthor().getId().equals(currentUser.getId());

        if (!isAdmin && !isInstructor && !isAuthor) {
            throw new AccessDeniedException("У вас нет прав на редактирование этого курса");
        }
    }

    private void checkEditPermissionForTest(Test test, User currentUser) {
        Course course = getCourseFromTest(test);
        checkEditPermission(course, currentUser);
    }

    private Course getCourseFromTest(Test test) {
        if (test.getLesson() != null)
            return test.getLesson().getModule().getCourse();
        if (test.getModule() != null)
            return test.getModule().getCourse();
        if (test.getCourse() != null)
            return test.getCourse();
        throw new IllegalStateException("Test must have a target");
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

    private String getTargetTitle(Test test) {
        if (test.getLesson() != null)
            return test.getLesson().getTitle();
        if (test.getModule() != null)
            return test.getModule().getTitle();
        if (test.getCourse() != null)
            return test.getCourse().getTitle();
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
                getTargetTitle(test));
    }

    private QuestionDetailResponse toQuestionDetailResponse(Question q) {
        return new QuestionDetailResponse(
                q.getId(),
                q.getText(),
                q.getOrderIndex(),
                q.getPoints(),
                q.getAnswerOptions().stream()
                        .map(this::toAnswerOptionDetailResponse)
                        .toList());
    }

    private AnswerOptionDetailResponse toAnswerOptionDetailResponse(AnswerOption o) {
        return new AnswerOptionDetailResponse(
                o.getId(),
                o.getText(),
                o.getOrderIndex(),
                o.isCorrect());
    }
}
