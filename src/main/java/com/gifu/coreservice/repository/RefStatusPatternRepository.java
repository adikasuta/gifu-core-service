package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.RefStatusPattern;
import com.gifu.coreservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefStatusPatternRepository extends JpaRepository<RefStatusPattern, Long> {
    Optional<RefStatusPattern> findByPatternCode(String patternCode);
}
