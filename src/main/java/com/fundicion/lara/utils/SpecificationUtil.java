package com.fundicion.lara.utils;

import com.fundicion.lara.commons.emuns.TransactionType;
import com.fundicion.lara.dto.request.RequestParams;
import com.fundicion.lara.entity.ProductEntity;
import com.fundicion.lara.entity.TransactionEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpecificationUtil {
    public static <T> Specification<T> getSpecificationByParams(RequestParams req, Class<T> entityClass) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // Agregar predicados
            if (req.getStartDate() != null && req.getEndDate() != null) {
                predicates.add(criteriaBuilder.between(root.get("operationDate"), req.getStartDate(), req.getEndDate()));
            }

            if (StringUtils.isNotBlank(req.getType())) {
                predicates.add(root.get("type").in(toCommaListTransactionType(req.getType())));
            }
            if (StringUtils.isNotBlank(req.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), req.getStatus()));
            }

            if (StringUtils.isNotBlank((req.getSearch()))) {
                if (ProductEntity.class.isAssignableFrom(entityClass)) {
                    String search = req.getSearch().toLowerCase();
                    predicates.add(
                            criteriaBuilder.like(
                                    criteriaBuilder.lower(root.get("name")),
                                    "%" + search + "%"
                            )
                    );
                } else if (TransactionEntity.class.isAssignableFrom(entityClass)) {
                    String search = req.getSearch().toLowerCase(); // Lo normalizas a minúsculas
                    String pattern = "%" + search + "%";

                    Predicate namePredicate = criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("invoiceNumber")),
                            pattern
                    );

                    Predicate clientPredicate = criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("issuerRfc")),
                            pattern
                    );

                    predicates.add(
                            criteriaBuilder.or(namePredicate, clientPredicate)
                    );
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static List<TransactionType> toCommaListTransactionType(String str) {
        List<TransactionType> transactionTypeList = Stream.of(str.split(","))
                .map(String::trim)
                .map(TransactionType::fromString)
                .collect(Collectors.toList());
        return transactionTypeList.isEmpty() ? Collections.singletonList(TransactionType.fromString(str)) : transactionTypeList;
    }
}

/*

String search = req.getSearch().toLowerCase(); // Lo normalizas a minúsculas

// Construir el patrón LIKE
String pattern = "%" + search + "%";

// Predicate para 'name'
Predicate namePredicate = criteriaBuilder.like(
    criteriaBuilder.lower(root.get("name")),
    pattern
);

// Predicate para 'client'
Predicate clientPredicate = criteriaBuilder.like(
    criteriaBuilder.lower(root.get("client")),
    pattern
);

// Unir ambos con OR
predicates.add(
    criteriaBuilder.or(namePredicate, clientPredicate)
);
 */