package org.bm.service.education.learning.domain.assessment;

import jakarta.persistence.*;
import lombok.*;
import org.bm.service.education.common.base.EntityBase;

@Entity
@Table(name = "answer_options", indexes = {
        @Index(name = "idx_answer_options_question", columnList = "question_id")
})
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AnswerOption extends EntityBase {

    @Setter
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Setter
    @Builder.Default
    @Column(nullable = false)
    private boolean isCorrect = false;

    @Setter
    @Column(nullable = false)
    private int orderIndex;

    @Setter(AccessLevel.PACKAGE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
}
