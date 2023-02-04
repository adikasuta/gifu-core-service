package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.RefStatusPattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefStatusPatternRepository extends JpaRepository<RefStatusPattern, Long> {
}
