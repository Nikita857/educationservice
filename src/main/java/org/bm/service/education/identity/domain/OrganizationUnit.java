package org.bm.service.education.identity.domain;

import jakarta.persistence.*;
import lombok.*;
import org.bm.service.education.common.base.EntityBase;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "organization_units", indexes = {
        @Index(name = "idx_org_units_code", columnList = "code"),
        @Index(name = "idx_org_units_parent", columnList = "parent_id")
})
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationUnit extends EntityBase {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private OrganizationUnit parent;

    @Builder.Default
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<OrganizationUnit> children = new ArrayList<>();
}
