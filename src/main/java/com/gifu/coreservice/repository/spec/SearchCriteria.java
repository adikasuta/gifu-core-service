package com.gifu.coreservice.repository.spec;

import com.gifu.coreservice.enumeration.SearchOperation;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchCriteria {
    private String key;
    private SearchOperation operation;
    private Object value;
}
