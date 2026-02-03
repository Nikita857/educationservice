package org.bm.service.education.compliance.domain.record;

import jakarta.persistence.*;
import lombok.*;
import org.bm.service.education.common.base.EntityBase;
import org.bm.service.education.compliance.domain.requirement.TrainingRequirement;
import org.bm.service.education.identity.domain.User;
import org.bm.service.education.learning.domain.enrollment.Enrollment;

import java.time.LocalDateTime;

@Entity
@Table(name = "compliance_records", indexes = {
        @Index(name = "idx_compliance_records_user", columnList = "user_id"),
        @Index(name = "idx_compliance_records_requirement", columnList = "requirement_id"),
        @Index(name = "idx_compliance_records_status", columnList = "status"),
        @Index(name = "idx_compliance_records_expires_at", columnList = "expiresAt")
})
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ComplianceRecord extends EntityBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requirement_id", nullable = false)
    private TrainingRequirement requirement;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplianceStatus status = ComplianceStatus.PENDING;

    private LocalDateTime completedAt;

    private LocalDateTime expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id")
    private Enrollment enrollment;

    // Business methods
    public void startProgress(Enrollment enrollment) {
        this.status = ComplianceStatus.IN_PROGRESS;
        this.enrollment = enrollment;
    }

    public void markCompliant(Integer validityDays) {
        this.status = ComplianceStatus.COMPLIANT;
        this.completedAt = LocalDateTime.now();
        if (validityDays != null) {
            this.expiresAt = this.completedAt.plusDays(validityDays);
        }
    }

    public void markOverdue() {
        this.status = ComplianceStatus.OVERDUE;
    }

    public void markExpired() {
        this.status = ComplianceStatus.EXPIRED;
    }
}
