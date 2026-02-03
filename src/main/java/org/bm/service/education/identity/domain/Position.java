package org.bm.service.education.identity.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bm.service.education.common.base.EntityBase;

@Entity
@Table(name = "positions", indexes = {
        @Index(name = "idx_positions_code", columnList = "code")
})
@Getter
@Setter
@NoArgsConstructor
public class Position extends EntityBase {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;
}
