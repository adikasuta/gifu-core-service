package com.gifu.coreservice.entity;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Table(name = "variant_type")
@Data
public class VariantType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "variant_type_name")
    private String variantTypeName;
    @Column(name = "variant_type_code")
    private String variantTypeCode;
    @Column(name = "is_can_be_paired")
    private Boolean isCanBePaired;

}
