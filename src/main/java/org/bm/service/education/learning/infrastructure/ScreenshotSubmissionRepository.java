package org.bm.service.education.learning.infrastructure;

import org.bm.service.education.learning.domain.progress.ScreenshotStatus;
import org.bm.service.education.learning.domain.progress.ScreenshotSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScreenshotSubmissionRepository extends JpaRepository<ScreenshotSubmission, UUID> {

    List<ScreenshotSubmission> findByLessonProgressIdOrderByCreatedAtDesc(UUID lessonProgressId);

    @Query("SELECT s FROM ScreenshotSubmission s WHERE s.lessonProgress.id = :lessonProgressId ORDER BY s.createdAt DESC LIMIT 1")
    Optional<ScreenshotSubmission> findLatestByLessonProgressId(UUID lessonProgressId);

    List<ScreenshotSubmission> findByStatusOrderByCreatedAtAsc(ScreenshotStatus status);

    @Query("SELECT s FROM ScreenshotSubmission s " +
            "JOIN s.lessonProgress lp " +
            "JOIN lp.lesson l " +
            "JOIN l.module m " +
            "WHERE m.course.id = :courseId AND s.status = :status " +
            "ORDER BY s.createdAt ASC")
    List<ScreenshotSubmission> findByCourseIdAndStatus(UUID courseId, ScreenshotStatus status);

    boolean existsByLessonProgressIdAndStatus(UUID lessonProgressId, ScreenshotStatus status);
}
