package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.RefStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefStatusRepository extends JpaRepository<RefStatus, Long> {
}
