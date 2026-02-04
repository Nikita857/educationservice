package org.bm.service.education.learning.infrastructure;

import org.bm.service.education.learning.domain.assessment.TestAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TestAttemptRepository extends JpaRepository<TestAttempt, UUID> {

    List<TestAttempt> findByUserIdAndTestId(UUID userId, UUID testId);

    @Query("SELECT COUNT(ta) FROM TestAttempt ta WHERE ta.user.id = :userId AND ta.test.id = :testId")
    long countByUserIdAndTestId(UUID userId, UUID testId);

    Page<TestAttempt> findByUserId(UUID userId, Pageable pageable);

    List<TestAttempt> findByUserId(UUID userId);

    Optional<TestAttempt> findByUserIdAndTestIdAndFinishedAtIsNull(UUID userId, UUID testId);

    @Query("SELECT ta FROM TestAttempt ta WHERE ta.user.id = :userId AND ta.test.id = :testId AND ta.isPassed = true")
    List<TestAttempt> findPassedAttempts(UUID userId, UUID testId);

    List<TestAttempt> findByTestId(UUID testId);

    @Query("SELECT ta FROM TestAttempt ta " +
            "JOIN ta.test t " +
            "LEFT JOIN t.lesson l " +
            "LEFT JOIN l.module m " +
            "LEFT JOIN t.module tm " +
            "LEFT JOIN t.course tc " +
            "WHERE COALESCE(m.course.id, tm.course.id, tc.id) = :courseId")
    List<TestAttempt> findByCourseId(UUID courseId);

    Optional<TestAttempt> findTopByUserIdAndTestIdOrderByFinishedAtDesc(UUID userId, UUID testId);
}
