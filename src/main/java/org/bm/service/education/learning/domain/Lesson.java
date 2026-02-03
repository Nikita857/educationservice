package org.bm.service.education.learning.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bm.service.education.common.base.EntityBase;
import org.bm.service.education.learning.domain.assessment.Test;

@Entity
@Table(name = "lessons", indexes = {
        @Index(name = "idx_lessons_module", columnList = "module_id")
})
@Getter
@Setter
@NoArgsConstructor
public class Lesson extends EntityBase {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "jsonb")
    private String content;

    private String videoUrl;

    @Column(nullable = false)
    private int orderIndex;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @OneToOne(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private Test test;
}
