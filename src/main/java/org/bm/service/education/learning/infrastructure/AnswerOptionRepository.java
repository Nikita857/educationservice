package org.bm.service.education.learning.infrastructure;

import org.bm.service.education.learning.domain.assessment.AnswerOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnswerOptionRepository extends JpaRepository<AnswerOption, UUID> {

    List<AnswerOption> findByQuestionIdOrderByOrderIndexAsc(UUID questionId);

    @Query("SELECT COALESCE(MAX(a.orderIndex), 0) FROM AnswerOption a WHERE a.question.id = :questionId")
    int findMaxOrderIndexByQuestionId(UUID questionId);
}
