package org.bm.service.education.compliance.domain.requirement;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bm.service.education.common.base.EntityBase;
import org.bm.service.education.identity.domain.OrganizationUnit;
import org.bm.service.education.identity.domain.Position;
import org.bm.service.education.learning.domain.Course;

@Entity
@Table(name = "training_requirements", indexes = {
        @Index(name = "idx_training_req_course", columnList = "course_id"),
        @Index(name = "idx_training_req_scope", columnList = "scopeType"),
        @Index(name = "idx_training_req_position", columnList = "target_position_id"),
        @Index(name = "idx_training_req_org_unit", columnList = "target_organization_unit_id")
})
@Getter
@Setter
@NoArgsConstructor
public class TrainingRequirement extends EntityBase {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequirementsScopeType scopeType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_position_id")
    private Position targetPosition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_organization_unit_id")
    private OrganizationUnit targetOrganizationUnit;

    private Integer validityPeriodDays;

    @Column(nullable = false)
    private boolean isMandatory = true;
}
