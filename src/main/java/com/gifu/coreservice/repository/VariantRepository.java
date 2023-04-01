package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface VariantRepository extends JpaRepository<Variant, Long>, JpaSpecificationExecutor<Variant> {
    List<Variant> findByVariantTypeCodeAndIsDeletedIsFalse(String variantTypeCode);
    List<Variant> findByIsDeletedIsFalse();
    List<Variant> findByIdIn(List<Long> ids);
    List<Variant> findByIdInAndIsDeleted(List<Long> ids, boolean isDeleted);
    default List<Variant> findByIds(String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
        return findByIdIn(idList);
    }
}
