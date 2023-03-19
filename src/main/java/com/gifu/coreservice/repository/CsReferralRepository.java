package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.CsReferral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CsReferralRepository extends JpaRepository<CsReferral, Long> {

    long countByToken(String token);

    @Query("FROM CsReferral where user_id = :userId and inactive_date is null")
    List<CsReferral> findActiveByUserId(Long userId);
}
