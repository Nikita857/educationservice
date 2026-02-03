package org.bm.service.education.learning.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bm.service.education.common.base.EntityBase;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "modules", indexes = {
        @Index(name = "idx_modules_course", columnList = "course_id")
})
@Getter
@Setter
@NoArgsConstructor
public class Module extends EntityBase {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private int orderIndex;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<Lesson> lessons = new ArrayList<>();
}
