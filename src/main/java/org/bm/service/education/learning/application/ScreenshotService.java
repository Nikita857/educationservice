package org.bm.service.education.learning.application;

import lombok.RequiredArgsConstructor;
import org.bm.service.education.common.exception.ResourceNotFoundException;
import org.bm.service.education.common.file.FileService;
import org.bm.service.education.common.file.FileType;
import org.bm.service.education.identity.domain.User;
import org.bm.service.education.identity.domain.UserRole;
import org.bm.service.education.learning.api.dto.request.ReviewScreenshotRequest;
import org.bm.service.education.learning.api.dto.response.ScreenshotSubmissionResponse;
import org.bm.service.education.learning.domain.Course;
import org.bm.service.education.learning.domain.Lesson;
import org.bm.service.education.learning.domain.LessonCompletionType;
import org.bm.service.education.learning.domain.progress.LessonProgress;
import org.bm.service.education.learning.domain.progress.ScreenshotStatus;
import org.bm.service.education.learning.domain.progress.ScreenshotSubmission;
import org.bm.service.education.learning.infrastructure.LessonProgressRepository;
import org.bm.service.education.learning.infrastructure.ScreenshotSubmissionRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScreenshotService {

    private final ScreenshotSubmissionRepository screenshotRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final FileService fileService;

    private static final int URL_EXPIRY_MINUTES = 120;

    /**
     * Загрузить скриншот подтверждения для урока
     */
    @Transactional
    public ScreenshotSubmissionResponse submitScreenshot(UUID lessonProgressId, MultipartFile file, User currentUser) {
        LessonProgress lessonProgress = lessonProgressRepository.findById(lessonProgressId)
                .orElseThrow(() -> new ResourceNotFoundException("Прогресс урока", lessonProgressId));

        // Проверка прав: только владелец прогресса может загружать
        if (!lessonProgress.getEnrollment().getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Вы можете загружать скриншоты только для своих уроков");
        }

        // Проверка типа урока
        Lesson lesson = lessonProgress.getLesson();
        if (lesson.getCompletionType() != LessonCompletionType.SCREENSHOT) {
            throw new IllegalStateException("Этот урок не требует скриншот подтверждения");
        }

        // Проверка что урок еще не завершен
        if (lessonProgress.isCompleted()) {
            throw new IllegalStateException("Урок уже завершен");
        }

        // Проверка что нет скриншота в статусе PENDING
        boolean hasPending = screenshotRepository.existsByLessonProgressIdAndStatus(lessonProgressId,
                ScreenshotStatus.PENDING);
        if (hasPending) {
            throw new IllegalStateException("У вас уже есть скриншот на проверке. Дождитесь результата.");
        }

        // Загружаем файл в MinIO
        String filePath = fileService.uploadFile(file, FileType.IMAGE, "screenshots");

        // Создаем submission
        ScreenshotSubmission submission = ScreenshotSubmission.builder()
                .lessonProgress(lessonProgress)
                .filePath(filePath)
                .status(ScreenshotStatus.PENDING)
                .build();

        ScreenshotSubmission saved = screenshotRepository.save(submission);
        return toResponse(saved);
    }

    /**
     * Получить скриншоты для урока (для студента)
     */
    public List<ScreenshotSubmissionResponse> getMyScreenshots(UUID lessonProgressId, User currentUser) {
        LessonProgress lessonProgress = lessonProgressRepository.findById(lessonProgressId)
                .orElseThrow(() -> new ResourceNotFoundException("Прогресс урока", lessonProgressId));

        // Проверка прав
        if (!lessonProgress.getEnrollment().getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Нет доступа к этому прогрессу урока");
        }

        return screenshotRepository.findByLessonProgressIdOrderByCreatedAtDesc(lessonProgressId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Получить скриншоты на проверку для курса (для инструктора)
     */
    public List<ScreenshotSubmissionResponse> getPendingScreenshots(UUID courseId, User currentUser) {
        checkInstructorPermission(courseId, currentUser);

        return screenshotRepository.findByCourseIdAndStatus(courseId, ScreenshotStatus.PENDING)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Рецензировать скриншот (одобрить/отклонить)
     */
    @Transactional
    public ScreenshotSubmissionResponse reviewScreenshot(UUID submissionId, ReviewScreenshotRequest request,
            User currentUser) {
        ScreenshotSubmission submission = screenshotRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Скриншот", submissionId));

        // Проверка прав инструктора
        Course course = submission.getLessonProgress().getLesson().getModule().getCourse();
        checkInstructorPermission(course.getId(), currentUser);

        // Проверка что скриншот в статусе PENDING
        if (submission.getStatus() != ScreenshotStatus.PENDING) {
            throw new IllegalStateException("Скриншот уже проверен");
        }

        if (request.approved()) {
            submission.approve(currentUser, request.comment());

            // Отмечаем урок как завершенный
            LessonProgress lessonProgress = submission.getLessonProgress();
            lessonProgress.markCompleted();
            lessonProgressRepository.save(lessonProgress);
        } else {
            submission.reject(currentUser, request.comment());
        }

        ScreenshotSubmission saved = screenshotRepository.save(submission);
        return toResponse(saved);
    }

    // === Private helpers ===

    private void checkInstructorPermission(UUID courseId, User currentUser) {
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
        boolean isInstructor = currentUser.getRole() == UserRole.INSTRUCTOR;

        if (!isAdmin && !isInstructor) {
            throw new AccessDeniedException("Только инструкторы могут проверять скриншоты");
        }
    }

    private ScreenshotSubmissionResponse toResponse(ScreenshotSubmission s) {
        LessonProgress lp = s.getLessonProgress();
        Lesson lesson = lp.getLesson();
        User student = lp.getEnrollment().getUser();

        // Генерируем presigned URL для просмотра скриншота
        String fileUrl = fileService.getPresignedUrl(s.getFilePath(), URL_EXPIRY_MINUTES);

        return new ScreenshotSubmissionResponse(
                s.getId(),
                lp.getId(),
                lesson.getId(),
                lesson.getTitle(),
                student.getFirstName() + " " + student.getLastName(),
                fileUrl,
                s.getStatus().name(),
                s.getReviewComment(),
                s.getReviewedBy() != null ? s.getReviewedBy().getId() : null,
                s.getReviewedBy() != null ? s.getReviewedBy().getFirstName() + " " + s.getReviewedBy().getLastName()
                        : null,
                s.getReviewedAt(),
                s.getCreatedAt());
    }
}
