package org.bm.service.education.learning.infrastructure;

import org.bm.service.education.learning.domain.enrollment.Enrollment;
import org.bm.service.education.learning.domain.enrollment.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {

    Optional<Enrollment> findByUserIdAndCourseId(UUID userId, UUID courseId);

    boolean existsByUserIdAndCourseId(UUID userId, UUID courseId);

    Page<Enrollment> findByUserId(UUID userId, Pageable pageable);

    Page<Enrollment> findByUserIdAndStatus(UUID userId, EnrollmentStatus status, Pageable pageable);

    Page<Enrollment> findByCourseId(UUID courseId, Pageable pageable);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.status = :status")
    long countByCourseIdAndStatus(UUID courseId, EnrollmentStatus status);
}
