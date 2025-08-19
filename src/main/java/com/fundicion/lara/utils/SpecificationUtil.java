package com.fundicion.lara.utils;
import com.fundicion.lara.dto.request.RequestParams;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class SpecificationUtil {
    public static <T> Specification<T> getSpecificationByParams(RequestParams req, Class<T> entityClass) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // Agregar predicados
            predicates.add(criteriaBuilder.between(root.get("operationDate"), req.getStartDate(), req.getEndDate()));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

