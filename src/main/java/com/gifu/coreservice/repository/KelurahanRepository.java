package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.Kelurahan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KelurahanRepository extends JpaRepository<Kelurahan, String> {
    List<Kelurahan> findByDistrictId(String districtId);
}
