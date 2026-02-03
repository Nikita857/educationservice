package org.bm.service.education.learning.domain.enrollment;

import jakarta.persistence.*;
import lombok.*;
import org.bm.service.education.common.base.EntityBase;
import org.bm.service.education.identity.domain.User;
import org.bm.service.education.learning.domain.Course;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "course_id" })
}, indexes = {
        @Index(name = "idx_enrollments_user", columnList = "user_id"),
        @Index(name = "idx_enrollments_course", columnList = "course_id"),
        @Index(name = "idx_enrollments_status", columnList = "status")
})
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Enrollment extends EntityBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status = EnrollmentStatus.ENROLLED;

    @Column(nullable = false)
    private LocalDateTime enrolledAt;

    private LocalDateTime completedAt;

    @Builder.Default
    @Column(nullable = false)
    private int progressPercent = 0;

    @Override
    protected void onCreate() {
        super.onCreate();
        if (enrolledAt == null) {
            enrolledAt = LocalDateTime.now();
        }
    }
}
