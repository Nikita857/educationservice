package org.bm.service.education.learning.infrastructure;

import org.bm.service.education.learning.domain.assessment.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TestRepository extends JpaRepository<Test, UUID> {

    Optional<Test> findByLessonId(UUID lessonId);

    Optional<Test> findByModuleId(UUID moduleId);

    Optional<Test> findByCourseId(UUID courseId);

    @Query("SELECT t FROM Test t LEFT JOIN FETCH t.questions q LEFT JOIN FETCH q.answerOptions WHERE t.id = :id")
    Optional<Test> findByIdWithQuestions(UUID id);

    boolean existsByLessonId(UUID lessonId);

    boolean existsByModuleId(UUID moduleId);

    boolean existsByCourseId(UUID courseId);
}
