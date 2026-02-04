package org.bm.service.education.learning.infrastructure;

import org.bm.service.education.learning.domain.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ModuleRepository extends JpaRepository<Module, UUID> {

    List<Module> findByCourseIdOrderByOrderIndexAsc(UUID courseId);

    @Query("SELECT COALESCE(MAX(m.orderIndex), 0) FROM Module m WHERE m.course.id = :courseId")
    int findMaxOrderIndexByCourseId(UUID courseId);

    @Query("SELECT m FROM Module m LEFT JOIN FETCH m.lessons WHERE m.id = :id")
    Optional<Module> findByIdWithLessons(UUID id);
}
