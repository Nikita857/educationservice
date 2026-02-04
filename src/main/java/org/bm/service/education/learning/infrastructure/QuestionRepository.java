package org.bm.service.education.learning.infrastructure;

import org.bm.service.education.learning.domain.assessment.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {

    List<Question> findByTestIdOrderByOrderIndexAsc(UUID testId);

    @Query("SELECT COALESCE(MAX(q.orderIndex), 0) FROM Question q WHERE q.test.id = :testId")
    int findMaxOrderIndexByTestId(UUID testId);

    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.answerOptions WHERE q.id = :id")
    Optional<Question> findByIdWithOptions(UUID id);
}
