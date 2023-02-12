package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.Step;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StepRepository extends JpaRepository<Step, Long>, JpaSpecificationExecutor<Step> {
}
