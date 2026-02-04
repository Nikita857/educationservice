package org.bm.service.education.learning.infrastructure;

import org.bm.service.education.learning.domain.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID> {

    List<Lesson> findByModuleIdOrderByOrderIndexAsc(UUID moduleId);

    @Query("SELECT COALESCE(MAX(l.orderIndex), 0) FROM Lesson l WHERE l.module.id = :moduleId")
    int findMaxOrderIndexByModuleId(UUID moduleId);
}
