package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.OrderVariant;
import com.gifu.coreservice.entity.OrderVariantPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderVariantPriceRepository extends JpaRepository<OrderVariantPrice, Long> {
}
