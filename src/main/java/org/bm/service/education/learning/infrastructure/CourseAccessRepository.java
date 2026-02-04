package org.bm.service.education.learning.infrastructure;

import org.bm.service.education.learning.domain.access.CourseAccess;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseAccessRepository extends JpaRepository<CourseAccess, UUID> {

    Optional<CourseAccess> findByCourseIdAndUserId(UUID courseId, UUID userId);

    boolean existsByCourseIdAndUserId(UUID courseId, UUID userId);

    Page<CourseAccess> findByCourseId(UUID courseId, Pageable pageable);

    Page<CourseAccess> findByUserId(UUID userId, Pageable pageable);

    @Query("SELECT ca FROM CourseAccess ca WHERE ca.course.id = :courseId AND ca.user.id = :userId AND (ca.expiresAt IS NULL OR ca.expiresAt > CURRENT_TIMESTAMP)")
    Optional<CourseAccess> findActiveAccess(UUID courseId, UUID userId);
}
