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
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Question extends EntityBase {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(nullable = false)
    private int orderIndex;

    @Builder.Default
    @Column(nullable = false)
    private int points = 1;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @Builder.Default
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<AnswerOption> answerOptions = new ArrayList<>();
}
