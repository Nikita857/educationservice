package org.bm.service.education.learning.infrastructure;

import org.bm.service.education.learning.domain.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID>, JpaSpecificationExecutor<Course> {

    List<Course> findByIsPublishedTrue();

    Page<Course> findByIsPublishedTrue(Pageable pageable);

    List<Course> findByAuthorId(UUID authorId);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.modules WHERE c.id = :id")
    Course findByIdWithModules(UUID id);
}
