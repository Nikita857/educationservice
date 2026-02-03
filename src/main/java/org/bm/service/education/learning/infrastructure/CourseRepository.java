package org.bm.service.education.learning.infrastructure;

import org.bm.service.education.learning.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {

    List<Course> findByIsPublishedTrue();

    List<Course> findByAuthorId(UUID authorId);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.modules WHERE c.id = :id")
    Course findByIdWithModules(UUID id);
}
