package org.bm.service.education.learning.domain.enrollment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@Getter
@Setter
@NoArgsConstructor
public class Enrollment extends EntityBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status = EnrollmentStatus.ENROLLED;

    @Column(nullable = false)
    private LocalDateTime enrolledAt;

    private LocalDateTime completedAt;

    @Column(nullable = false)
    private int progressPercent = 0;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
        if (enrolledAt == null) {
            enrolledAt = LocalDateTime.now();
        }
    }
}
