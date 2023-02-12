package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Long>, JpaSpecificationExecutor<Workflow> {
}
