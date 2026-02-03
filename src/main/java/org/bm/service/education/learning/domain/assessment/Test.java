package org.bm.service.education.learning.domain.assessment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bm.service.education.common.base.EntityBase;
import org.bm.service.education.learning.domain.Lesson;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tests", indexes = {
        @Index(name = "idx_tests_lesson", columnList = "lesson_id")
})
@Getter
@Setter
@NoArgsConstructor
public class Test extends EntityBase {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private int passingScore = 70;

    private Integer timeLimitMinutes;

    @Column(nullable = false)
    private int maxAttempts = 0;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<Question> questions = new ArrayList<>();
}
