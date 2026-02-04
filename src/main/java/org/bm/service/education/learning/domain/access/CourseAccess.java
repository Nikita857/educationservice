package org.bm.service.education.learning.domain.access;

import jakarta.persistence.*;
import lombok.*;
import org.bm.service.education.common.base.EntityBase;
import org.bm.service.education.identity.domain.User;
import org.bm.service.education.learning.domain.Course;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_access", indexes = {
        @Index(name = "idx_course_access_course", columnList = "course_id"),
        @Index(name = "idx_course_access_user", columnList = "user_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uc_course_access_user_course", columnNames = { "course_id", "user_id" })
})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseAccess extends EntityBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granted_by_id", nullable = false)
    private User grantedBy;

    @Setter
    private LocalDateTime expiresAt;

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isActive() {
        return !isExpired();
    }
}
