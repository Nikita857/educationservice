package org.bm.service.education.learning.domain;

import jakarta.persistence.*;
import lombok.*;
import org.bm.service.education.common.base.EntityBase;
import org.bm.service.education.learning.domain.assessment.Test;

@Entity
@Table(name = "lessons", indexes = {
        @Index(name = "idx_lessons_module", columnList = "module_id")
})
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Lesson extends EntityBase {

    @Setter
    @Column(nullable = false)
    private String title;

    @Setter
    @Column(columnDefinition = "jsonb")
    private String content;

    @Setter
    private String videoUrl;

    @Setter
    @Column(nullable = false)
    private int orderIndex;

    @Setter(AccessLevel.PACKAGE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @OneToOne(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private Test test;

    // Business methods
    public void assignTest(Test test) {
        this.test = test;
        test.setLesson(this);
    }

    public void removeTest() {
        if (this.test != null) {
            this.test.setLesson(null);
            this.test = null;
        }
    }
}
