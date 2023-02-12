package com.gifu.coreservice.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user")
@Data
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "employee_code")
    private String employeeCode;
    private String name;
    private String email;
    private String password;
    @Column(name = "inactive_date")
    private ZonedDateTime inactiveDate;
    @Column(name = "role_id")
    private Long roleId;
    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;

    @Column(name = "is_account_non_expired")
    private Boolean isAccountNonExpired;
    @Column(name = "is_account_non_locked")
    private Boolean isAccountNonLocked;
    @Column(name = "is_credentials_non_expired")
    private Boolean isCredentialsNonExpired;
    @Column(name = "is_enabled")
    private Boolean isEnabled;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private Set<CsReferral> csReferral;

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_permission",
            inverseJoinColumns = {@JoinColumn(name = "permission_id")},
            joinColumns = {@JoinColumn(name = "user_id")}
    )
    private Set<Permission> permissions;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissions;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
