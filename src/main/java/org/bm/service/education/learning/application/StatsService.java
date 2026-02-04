package org.bm.service.education.learning.application;

import lombok.RequiredArgsConstructor;
import org.bm.service.education.common.exception.ResourceNotFoundException;
import org.bm.service.education.identity.domain.User;
import org.bm.service.education.identity.domain.UserRole;
import org.bm.service.education.learning.api.dto.response.*;
import org.bm.service.education.learning.domain.Course;
import org.bm.service.education.learning.domain.Lesson;
import org.bm.service.education.learning.domain.LessonCompletionType;
import org.bm.service.education.learning.domain.Module;
import org.bm.service.education.learning.domain.assessment.TestAttempt;
import org.bm.service.education.learning.domain.enrollment.Enrollment;
import org.bm.service.education.learning.domain.enrollment.EnrollmentStatus;
import org.bm.service.education.learning.domain.progress.LessonProgress;
import org.bm.service.education.learning.domain.progress.ScreenshotStatus;
import org.bm.service.education.learning.domain.progress.ScreenshotSubmission;
import org.bm.service.education.learning.infrastructure.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final TestAttemptRepository testAttemptRepository;
    private final ScreenshotSubmissionRepository screenshotRepository;

    /**
     * Общая статистика курса для инструктора
     */
    public CourseStatsResponse getCourseStats(UUID courseId, User currentUser) {
        Course course = findCourseOrThrow(courseId);
        checkInstructorPermission(course, currentUser);

        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);

        // Подсчет студентов по статусам
        int totalStudents = enrollments.size();
        int activeStudents = (int) enrollments.stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.IN_PROGRESS).count();
        int completedStudents = (int) enrollments.stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.COMPLETED).count();
        int droppedStudents = (int) enrollments.stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.DROPPED).count();

        // Средний прогресс
        double averageProgress = enrollments.stream()
                .mapToInt(Enrollment::getProgressPercent)
                .average()
                .orElse(0.0);

        // Получаем все уроки курса
        List<Lesson> allLessons = course.getModules().stream()
                .flatMap(m -> m.getLessons().stream())
                .toList();
        int totalLessons = allLessons.size();

        // Статистика по урокам
        List<LessonStatsResponse> lessonStats = allLessons.stream()
                .map(lesson -> getLessonStats(lesson, enrollments))
                .toList();

        // Статистика по тестам
        List<TestAttempt> allAttempts = testAttemptRepository.findByCourseId(courseId);
        int totalTestAttempts = allAttempts.size();
        int passedAttempts = (int) allAttempts.stream().filter(TestAttempt::isPassed).count();
        double avgTestScore = allAttempts.stream()
                .filter(a -> a.getScore() != null)
                .mapToInt(TestAttempt::getScore)
                .average()
                .orElse(0.0);
        double passRate = totalTestAttempts > 0 ? (passedAttempts * 100.0) / totalTestAttempts : 0.0;

        // Количество тестов в курсе
        long totalTests = allLessons.stream().filter(l -> l.getTest() != null).count();

        // Скриншоты на проверку
        int pendingScreenshots = (int) screenshotRepository
                .findByCourseIdAndStatus(courseId, ScreenshotStatus.PENDING).size();

        return new CourseStatsResponse(
                totalStudents,
                activeStudents,
                completedStudents,
                droppedStudents,
                Math.round(averageProgress * 10) / 10.0,
                totalLessons,
                lessonStats,
                (int) totalTests,
                Math.round(avgTestScore * 10) / 10.0,
                totalTestAttempts,
                passedAttempts,
                Math.round(passRate * 10) / 10.0,
                pendingScreenshots);
    }

    /**
     * Список студентов с их прогрессом
     */
    public List<StudentProgressResponse> getStudentsProgress(UUID courseId, User currentUser) {
        Course course = findCourseOrThrow(courseId);
        checkInstructorPermission(course, currentUser);

        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);

        List<Lesson> allLessons = course.getModules().stream()
                .flatMap(m -> m.getLessons().stream())
                .toList();

        return enrollments.stream()
                .map(e -> getStudentProgress(e, allLessons))
                .toList();
    }

    /**
     * Детальный прогресс конкретного студента
     */
    public StudentProgressResponse getStudentProgressDetail(UUID enrollmentId, User currentUser) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Запись на курс", enrollmentId));

        checkInstructorPermission(enrollment.getCourse(), currentUser);

        List<Lesson> allLessons = enrollment.getCourse().getModules().stream()
                .flatMap(m -> m.getLessons().stream())
                .toList();

        return getStudentProgress(enrollment, allLessons);
    }

    // === Private helpers ===

    private LessonStatsResponse getLessonStats(Lesson lesson, List<Enrollment> enrollments) {
        int totalStudents = enrollments.size();

        // Считаем завершения
        long completedCount = enrollments.stream()
                .filter(e -> isLessonCompleted(e.getId(), lesson.getId()))
                .count();

        double completionRate = totalStudents > 0 ? (completedCount * 100.0) / totalStudents : 0.0;

        // Средний балл по тесту (если есть)
        Double avgTestScore = null;
        if (lesson.getTest() != null) {
            avgTestScore = testAttemptRepository.findByTestId(lesson.getTest().getId())
                    .stream()
                    .filter(a -> a.getScore() != null)
                    .mapToInt(TestAttempt::getScore)
                    .average()
                    .orElse(0.0);
        }

        // Скриншоты на проверку
        int pendingScreenshots = 0;
        if (lesson.getCompletionType() == LessonCompletionType.SCREENSHOT) {
            // Считаем pending скриншоты через урок
            pendingScreenshots = countPendingScreenshotsForLesson(lesson.getId());
        }

        return new LessonStatsResponse(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getModule().getTitle(),
                lesson.getCompletionType().name(),
                totalStudents,
                (int) completedCount,
                Math.round(completionRate * 10) / 10.0,
                avgTestScore != null ? Math.round(avgTestScore * 10) / 10.0 : null,
                pendingScreenshots);
    }

    private StudentProgressResponse getStudentProgress(Enrollment enrollment, List<Lesson> allLessons) {
        User student = enrollment.getUser();
        int totalLessons = allLessons.size();

        // Получаем прогресс по урокам
        List<LessonProgress> progressList = lessonProgressRepository.findByEnrollmentId(enrollment.getId());
        Map<UUID, LessonProgress> progressMap = new HashMap<>();
        for (LessonProgress lp : progressList) {
            progressMap.put(lp.getLesson().getId(), lp);
        }

        int completedLessons = (int) progressList.stream().filter(LessonProgress::isCompleted).count();

        // Детальный прогресс по урокам
        List<StudentLessonProgress> lessons = allLessons.stream()
                .map(lesson -> {
                    LessonProgress lp = progressMap.get(lesson.getId());

                    Integer testScore = null;
                    Boolean testPassed = null;
                    if (lesson.getTest() != null) {
                        TestAttempt lastAttempt = testAttemptRepository
                                .findTopByUserIdAndTestIdOrderByFinishedAtDesc(
                                        student.getId(), lesson.getTest().getId())
                                .orElse(null);
                        if (lastAttempt != null) {
                            testScore = lastAttempt.getScore();
                            testPassed = lastAttempt.isPassed();
                        }
                    }

                    String screenshotStatus = null;
                    if (lesson.getCompletionType() == LessonCompletionType.SCREENSHOT && lp != null) {
                        ScreenshotSubmission screenshot = screenshotRepository
                                .findLatestByLessonProgressId(lp.getId())
                                .orElse(null);
                        if (screenshot != null) {
                            screenshotStatus = screenshot.getStatus().name();
                        }
                    }

                    return new StudentLessonProgress(
                            lesson.getId(),
                            lesson.getTitle(),
                            lesson.getModule().getTitle(),
                            lesson.getCompletionType().name(),
                            lp != null && lp.isCompleted(),
                            lp != null ? lp.getCompletedAt() : null,
                            testScore,
                            testPassed,
                            screenshotStatus);
                })
                .toList();

        // Статистика по тестам студента
        List<TestAttempt> studentAttempts = testAttemptRepository.findByUserId(student.getId());
        int passedTests = (int) studentAttempts.stream().filter(TestAttempt::isPassed).count();
        int totalTests = (int) allLessons.stream().filter(l -> l.getTest() != null).count();
        Double avgScore = studentAttempts.stream()
                .filter(a -> a.getScore() != null)
                .mapToInt(TestAttempt::getScore)
                .average()
                .orElse(0.0);

        return new StudentProgressResponse(
                enrollment.getId(),
                student.getId(),
                student.getFirstName() + " " + student.getLastName(),
                student.getEmail(),
                enrollment.getStatus().name(),
                enrollment.getProgressPercent(),
                enrollment.getEnrolledAt(),
                getLastActivityAt(progressList),
                completedLessons,
                totalLessons,
                lessons,
                passedTests,
                totalTests,
                Math.round(avgScore * 10) / 10.0);
    }

    private boolean isLessonCompleted(UUID enrollmentId, UUID lessonId) {
        return lessonProgressRepository
                .findByEnrollmentIdAndLessonId(enrollmentId, lessonId)
                .map(LessonProgress::isCompleted)
                .orElse(false);
    }

    private int countPendingScreenshotsForLesson(UUID lessonId) {
        // Упрощенный подсчет через репозиторий
        // В реальности нужен отдельный метод в репозитории
        return 0; // TODO: добавить метод в репозиторий
    }

    private java.time.LocalDateTime getLastActivityAt(List<LessonProgress> progressList) {
        return progressList.stream()
                .map(LessonProgress::getCompletedAt)
                .filter(Objects::nonNull)
                .max(java.time.LocalDateTime::compareTo)
                .orElse(null);
    }

    private Course findCourseOrThrow(UUID id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Курс", id));
    }

    private void checkInstructorPermission(Course course, User currentUser) {
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
        boolean isInstructor = currentUser.getRole() == UserRole.INSTRUCTOR;
        boolean isAuthor = course.getAuthor() != null &&
                course.getAuthor().getId().equals(currentUser.getId());

        if (!isAdmin && !isInstructor && !isAuthor) {
            throw new AccessDeniedException("Нет доступа к статистике этого курса");
        }
    }
}
