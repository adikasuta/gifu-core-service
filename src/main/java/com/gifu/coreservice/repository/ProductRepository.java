package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.Product;
import com.gifu.coreservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Lob;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findByProductCategoryIdIn(@Param("productCategoryId") List<Long> productCategoryId);
    List<Product> findByProductCategoryId(Long productCategoryId);

    long countByProductCode(String productCode);
}
