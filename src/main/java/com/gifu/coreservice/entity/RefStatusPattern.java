package com.gifu.coreservice.entity;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "ref_status_pattern")
@Data
public class RefStatusPattern {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @Column(name = "pattern_code")
    private String patternCode;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "pattern_code_id")
    private List<RefStatus> refStatuses;
}
