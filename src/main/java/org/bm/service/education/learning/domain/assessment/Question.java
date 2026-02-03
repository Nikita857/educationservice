package org.bm.service.education.learning.domain.assessment;

import jakarta.persistence.*;
import lombok.*;
import org.bm.service.education.common.base.EntityBase;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions", indexes = {
        @Index(name = "idx_questions_test", columnList = "test_id")
})
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Question extends EntityBase {

    @Setter
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Setter
    @Column(nullable = false)
    private int orderIndex;

    @Setter
    @Builder.Default
    @Column(nullable = false)
    private int points = 1;

    @Setter(AccessLevel.PACKAGE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @Builder.Default
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<AnswerOption> answerOptions = new ArrayList<>();

    // Business methods
    public void addAnswerOption(AnswerOption option) {
        answerOptions.add(option);
        option.setQuestion(this);
    }

    public void removeAnswerOption(AnswerOption option) {
        answerOptions.remove(option);
        option.setQuestion(null);
    }
}
