package com.gifu.coreservice.repository.spec;

import com.gifu.coreservice.exception.InvalidRequestException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicSpec<T> implements Specification<T> {

    private SearchCriteria criteria;

    @SneakyThrows
    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        switch (criteria.getOperation()) {
            case EQUALS:
                if (root.get(criteria.getKey()).getJavaType() == String.class) {
                    String val = (String) (criteria.getValue());
                    return builder.equal(
                            root.get(criteria.getKey()), val);
                }
                return builder.equal(
                        root.get(criteria.getKey()), criteria.getValue());
            case LIKE:
                if (root.get(criteria.getKey()).getJavaType() == String.class) {
                    String val = (String) (criteria.getValue());
                    return builder.like(
                            builder.upper(root.get(criteria.getKey())),
                            "%"+val.toUpperCase()+"%");
                } else {
                    return builder.equal(root.get(criteria.getKey()), criteria.getValue());
                }
            case GREATER_THAN_EQUALS:
                return builder.greaterThanOrEqualTo(
                        root.get(criteria.getKey()), criteria.getValue().toString());
            case LESSER_THAN_EQUALS:
                return builder.lessThanOrEqualTo(
                        root.get(criteria.getKey()), criteria.getValue().toString());
            case LESSER_THAN:
                return builder.lessThan(
                        root.get(criteria.getKey()), criteria.getValue().toString());
            case GREATER_THAN:
                return builder.greaterThan(
                        root.get(criteria.getKey()), criteria.getValue().toString());
            case IN:
                if (!(criteria.getValue() instanceof List)) {
                    throw new InvalidRequestException("Only support multiple object type", null);
                }
                CriteriaBuilder.In<List<Object>> in = builder.in(
                        root.get(criteria.getKey()));
                return in.value((List<Object>) criteria.getValue());
            case NOT_EQUALS:
                return builder.notEqual(
                        root.get(criteria.getKey()), criteria.getValue().toString());
            case IS_NULL:
                return root.get(criteria.getKey()).isNull();
        }
        return null;
    }
}
