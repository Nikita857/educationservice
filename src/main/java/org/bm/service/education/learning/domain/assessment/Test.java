package org.bm.service.education.learning.domain.assessment;

import jakarta.persistence.*;
import lombok.*;
import org.bm.service.education.common.base.EntityBase;
import org.bm.service.education.learning.domain.Lesson;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tests", indexes = {
        @Index(name = "idx_tests_lesson", columnList = "lesson_id")
})
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Test extends EntityBase {

    @Setter
    @Column(nullable = false)
    private String title;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String description;

    @Setter
    @Builder.Default
    @Column(nullable = false)
    private int passingScore = 70;

    @Setter
    private Integer timeLimitMinutes;

    @Setter
    @Builder.Default
    @Column(nullable = false)
    private int maxAttempts = 0;

    @Setter(AccessLevel.PACKAGE)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Builder.Default
    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<Question> questions = new ArrayList<>();

    // Business methods
    public void addQuestion(Question question) {
        questions.add(question);
        question.setTest(this);
    }

    public void removeQuestion(Question question) {
        questions.remove(question);
        question.setTest(null);
    }
}
