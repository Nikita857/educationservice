package org.bm.service.education.learning.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bm.service.education.common.base.EntityBase;
import org.bm.service.education.identity.domain.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses", indexes = {
        @Index(name = "idx_courses_author", columnList = "author_id"),
        @Index(name = "idx_courses_published", columnList = "isPublished")
})
@Getter
@Setter
@NoArgsConstructor
public class Course extends EntityBase {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String thumbnailUrl;

    @Column(nullable = false)
    private boolean isPublished = false;

    private Integer estimatedDurationMinutes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<Module> modules = new ArrayList<>();
}
