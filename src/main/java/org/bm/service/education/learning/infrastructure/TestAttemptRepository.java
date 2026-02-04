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

    Optional<TestAttempt> findByUserIdAndTestIdAndFinishedAtIsNull(UUID userId, UUID testId);

    @Query("SELECT ta FROM TestAttempt ta WHERE ta.user.id = :userId AND ta.test.id = :testId AND ta.isPassed = true")
    List<TestAttempt> findPassedAttempts(UUID userId, UUID testId);
}
