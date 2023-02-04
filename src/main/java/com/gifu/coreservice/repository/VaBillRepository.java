package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.VaBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VaBillRepository extends JpaRepository<VaBill, Long> {
}
