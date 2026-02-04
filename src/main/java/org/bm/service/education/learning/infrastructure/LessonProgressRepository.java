package org.bm.service.education.learning.infrastructure;

import org.bm.service.education.learning.domain.progress.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, UUID> {

    Optional<LessonProgress> findByEnrollmentIdAndLessonId(UUID enrollmentId, UUID lessonId);

    List<LessonProgress> findByEnrollmentId(UUID enrollmentId);

    @Query("SELECT COUNT(lp) FROM LessonProgress lp WHERE lp.enrollment.id = :enrollmentId AND lp.isCompleted = true")
    long countCompletedByEnrollmentId(UUID enrollmentId);

    boolean existsByEnrollmentIdAndLessonIdAndIsCompletedTrue(UUID enrollmentId, UUID lessonId);
}
