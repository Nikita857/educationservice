package org.bm.service.education.identity.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bm.service.education.common.base.EntityBase;

import java.time.LocalDate;

@Entity
@Table(name = "user_assignments", indexes = {
        @Index(name = "idx_user_assignments_user", columnList = "user_id"),
        @Index(name = "idx_user_assignments_position", columnList = "position_id"),
        @Index(name = "idx_user_assignments_org_unit", columnList = "organization_unit_id")
})
@Getter
@Setter
@NoArgsConstructor
public class UserAssignment extends EntityBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_unit_id", nullable = false)
    private OrganizationUnit organizationUnit;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @Column(nullable = false)
    private boolean isPrimary = true;
}
