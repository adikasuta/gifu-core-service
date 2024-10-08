package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.Product;
import com.gifu.coreservice.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long>, JpaSpecificationExecutor<ProductCategory> {
    List<ProductCategory> findByWorkflowCode(String workflowCode);
}
