package org.bm.service.education.learning.application;

import lombok.RequiredArgsConstructor;
import org.bm.service.education.common.api.PaginatedResponse;
import org.bm.service.education.common.exception.ResourceNotFoundException;
import org.bm.service.education.identity.domain.User;
import org.bm.service.education.learning.api.dto.response.EnrollmentResponse;
import org.bm.service.education.learning.api.dto.response.LessonProgressResponse;
import org.bm.service.education.learning.domain.Course;
import org.bm.service.education.learning.domain.Lesson;
import org.bm.service.education.learning.domain.enrollment.Enrollment;
import org.bm.service.education.learning.domain.progress.LessonProgress;
import org.bm.service.education.learning.infrastructure.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;

    // === Записи на курсы ===

    @Transactional
    public EnrollmentResponse enrollInCourse(UUID courseId, User user) {
        Course course = findCourseOrThrow(courseId);

        if (enrollmentRepository.existsByUserIdAndCourseId(user.getId(), courseId)) {
            throw new IllegalStateException("Вы уже записаны на этот курс");
        }

        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);
        return toResponse(saved);
    }

    public EnrollmentResponse getMyEnrollment(UUID courseId, User user) {
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(user.getId(), courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Запись на курс", courseId));
        return toResponse(enrollment);
    }

    public PaginatedResponse<EnrollmentResponse> getMyEnrollments(User user, int page, int size, String path) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("enrolledAt").descending());
        Page<Enrollment> enrollments = enrollmentRepository.findByUserId(user.getId(), pageable);
        return PaginatedResponse.of(enrollments.map(this::toResponse), path);
    }

    @Transactional
    public void dropEnrollment(UUID courseId, User user) {
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(user.getId(), courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Запись на курс", courseId));
        enrollment.drop();
        enrollmentRepository.save(enrollment);
    }

    // === Прогресс по урокам ===

    @Transactional
    public LessonProgressResponse markLessonCompleted(UUID lessonId, User user) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Урок", lessonId));

        // Проверяем способ закрытия урока
        switch (lesson.getCompletionType()) {
            case TEST -> throw new IllegalStateException(
                    "Этот урок закрывается после прохождения теста");
            case SCREENSHOT -> throw new IllegalStateException(
                    "Этот урок закрывается после одобрения скриншота подтверждения");
            case READ -> {
                // OK, можно отметить вручную
            }
        }

        UUID courseId = lesson.getModule().getCourse().getId();
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(user.getId(), courseId)
                .orElseThrow(() -> new IllegalStateException("Вы не записаны на этот курс"));

        return completeLessonInternal(enrollment, lesson);
    }

    /**
     * Внутренний метод для завершения урока (вызывается из TestService и
     * ScreenshotService)
     */
    @Transactional
    public LessonProgressResponse completeLessonInternal(Enrollment enrollment, Lesson lesson) {
        // Начинаем прогресс если только что записались
        enrollment.startProgress();

        // Ищем или создаем прогресс урока
        LessonProgress progress = lessonProgressRepository
                .findByEnrollmentIdAndLessonId(enrollment.getId(), lesson.getId())
                .orElseGet(() -> LessonProgress.builder()
                        .enrollment(enrollment)
                        .lesson(lesson)
                        .build());

        if (progress.isCompleted()) {
            return toProgressResponse(progress);
        }

        progress.markCompleted();
        lessonProgressRepository.save(progress);

        // Пересчитываем общий прогресс курса
        recalculateCourseProgress(enrollment);
        enrollmentRepository.save(enrollment);

        return toProgressResponse(progress);
    }

    public List<LessonProgressResponse> getLessonProgress(UUID courseId, User user) {
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(user.getId(), courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Запись на курс", courseId));

        return lessonProgressRepository.findByEnrollmentId(enrollment.getId())
                .stream()
                .map(this::toProgressResponse)
                .toList();
    }

    // === Private helpers ===

    private void recalculateCourseProgress(Enrollment enrollment) {
        Course course = enrollment.getCourse();
        long totalLessons = course.getModules().stream()
                .mapToLong(m -> m.getLessons().size())
                .sum();

        if (totalLessons == 0)
            return;

        long completedLessons = lessonProgressRepository.countCompletedByEnrollmentId(enrollment.getId());
        int percent = (int) ((completedLessons * 100) / totalLessons);
        enrollment.updateProgress(percent);
    }

    private Course findCourseOrThrow(UUID id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Курс", id));
    }

    private EnrollmentResponse toResponse(Enrollment e) {
        return new EnrollmentResponse(
                e.getId(),
                e.getCourse().getId(),
                e.getCourse().getTitle(),
                e.getUser().getId(),
                e.getUser().getLastName() + " " + e.getUser().getFirstName(),
                e.getStatus().name(),
                e.getProgressPercent(),
                e.getEnrolledAt(),
                e.getCompletedAt());
    }

    private LessonProgressResponse toProgressResponse(LessonProgress lp) {
        return new LessonProgressResponse(
                lp.getLesson().getId(),
                lp.getLesson().getTitle(),
                lp.isCompleted(),
                lp.getCompletedAt());
    }
}
