package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.Kelurahan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KelurahanRepository extends JpaRepository<Kelurahan, Long> {
}
