package org.bm.service.education.learning.domain.progress;

import jakarta.persistence.*;
import lombok.*;
import org.bm.service.education.common.base.EntityBase;
import org.bm.service.education.learning.domain.Lesson;
import org.bm.service.education.learning.domain.enrollment.Enrollment;

import java.time.LocalDateTime;

@Entity
@Table(name = "lesson_progress", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "enrollment_id", "lesson_id" })
}, indexes = {
        @Index(name = "idx_lesson_progress_enrollment", columnList = "enrollment_id"),
        @Index(name = "idx_lesson_progress_lesson", columnList = "lesson_id")
})
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LessonProgress extends EntityBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Builder.Default
    @Column(nullable = false)
    private boolean isCompleted = false;

    private LocalDateTime completedAt;
}
