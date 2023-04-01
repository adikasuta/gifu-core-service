package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, String> {
    List<City> findByProvinceId(String provinceId);
}
