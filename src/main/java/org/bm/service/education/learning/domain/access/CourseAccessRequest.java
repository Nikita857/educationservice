package org.bm.service.education.learning.domain.access;

import jakarta.persistence.*;
import lombok.*;
import org.bm.service.education.common.base.EntityBase;
import org.bm.service.education.identity.domain.User;
import org.bm.service.education.learning.domain.Course;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_access_requests", indexes = {
        @Index(name = "idx_access_req_course", columnList = "course_id"),
        @Index(name = "idx_access_req_user", columnList = "user_id"),
        @Index(name = "idx_access_req_status", columnList = "status")
})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseAccessRequest extends EntityBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by_id", nullable = false)
    private User requestedBy;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CourseAccessRequestStatus status = CourseAccessRequestStatus.PENDING;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String reason;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by_id")
    private User resolvedBy;

    @Setter
    private LocalDateTime resolvedAt;

    public void approve(User instructor) {
        this.status = CourseAccessRequestStatus.APPROVED;
        this.resolvedBy = instructor;
        this.resolvedAt = LocalDateTime.now();
    }

    public void reject(User instructor) {
        this.status = CourseAccessRequestStatus.REJECTED;
        this.resolvedBy = instructor;
        this.resolvedAt = LocalDateTime.now();
    }
}
