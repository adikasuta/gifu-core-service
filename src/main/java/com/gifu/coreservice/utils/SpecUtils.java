package com.gifu.coreservice.utils;

import com.gifu.coreservice.enumeration.SearchOperation;
import com.gifu.coreservice.repository.spec.BasicSpec;
import com.gifu.coreservice.repository.spec.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

public class SpecUtils<T> {
    public Specification<T> isNotTrue(String field){
        BasicSpec<T> fieldTrue = new BasicSpec<>(new SearchCriteria(field, SearchOperation.EQUALS, true));
        BasicSpec<T> fieldEmpty = new BasicSpec<>(new SearchCriteria(field, SearchOperation.IS_NULL, null));

        return Specification.not(fieldTrue).or(fieldEmpty);
    }
}
