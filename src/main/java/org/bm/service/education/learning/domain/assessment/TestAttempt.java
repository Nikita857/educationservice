package org.bm.service.education.learning.domain.assessment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bm.service.education.common.base.EntityBase;
import org.bm.service.education.identity.domain.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "test_attempts", indexes = {
        @Index(name = "idx_test_attempts_user", columnList = "user_id"),
        @Index(name = "idx_test_attempts_test", columnList = "test_id"),
        @Index(name = "idx_test_attempts_user_test", columnList = "user_id, test_id")
})
@Getter
@Setter
@NoArgsConstructor
public class TestAttempt extends EntityBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    private Integer score;

    @Column(nullable = false)
    private boolean isPassed = false;
}
