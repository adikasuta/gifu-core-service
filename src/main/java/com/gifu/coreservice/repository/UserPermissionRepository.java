package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, Long>, JpaSpecificationExecutor<UserPermission> {
}
