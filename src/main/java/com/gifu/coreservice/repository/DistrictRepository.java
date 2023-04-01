package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepository extends JpaRepository<District, String> {
    List<District> findByCityId(String cityId);
}
