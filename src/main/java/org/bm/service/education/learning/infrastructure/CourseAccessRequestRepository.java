package org.bm.service.education.learning.infrastructure;

import org.bm.service.education.learning.domain.access.CourseAccessRequest;
import org.bm.service.education.learning.domain.access.CourseAccessRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseAccessRequestRepository extends JpaRepository<CourseAccessRequest, UUID> {

    Page<CourseAccessRequest> findByCourseIdAndStatus(UUID courseId, CourseAccessRequestStatus status,
            Pageable pageable);

    Page<CourseAccessRequest> findByCourseId(UUID courseId, Pageable pageable);

    List<CourseAccessRequest> findByUserIdAndStatus(UUID userId, CourseAccessRequestStatus status);

    boolean existsByCourseIdAndUserIdAndStatus(UUID courseId, UUID userId, CourseAccessRequestStatus status);
}
