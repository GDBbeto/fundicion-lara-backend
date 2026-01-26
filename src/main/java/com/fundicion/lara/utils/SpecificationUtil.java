package com.fundicion.lara.utils;

import com.fundicion.lara.commons.emuns.TransactionType;
import com.fundicion.lara.dto.request.RequestParams;
import com.fundicion.lara.entity.OrderTransactionEntity;
import com.fundicion.lara.entity.ProductEntity;
import com.fundicion.lara.entity.TransactionEntity;
import com.fundicion.lara.entity.UserEntity;
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
                // Lo normalizas a minúsculas
                String search = req.getSearch().toLowerCase();
                String pattern = "%" + search + "%";
                if (ProductEntity.class.isAssignableFrom(entityClass)) {
                    Predicate namePredicate = criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("name")),
                            pattern
                    );

                    if (StringUtils.isNotBlank(req.getClient())) {
                        predicates.add(
                                criteriaBuilder.equal(
                                        criteriaBuilder.lower(root.get("client")),
                                        req.getClient().toLowerCase()
                                )
                        );

                        predicates.add(
                                criteriaBuilder.or(namePredicate)
                        );
                    } else {
                        Predicate clientPredicate = criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("client")),
                                pattern
                        );
                        predicates.add(
                                criteriaBuilder.or(namePredicate, clientPredicate)
                        );
                    }

                } else if (TransactionEntity.class.isAssignableFrom(entityClass)) {
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
                } else if (OrderTransactionEntity.class.isAssignableFrom(entityClass)) {
                    Predicate namePredicate = criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("description")),
                            pattern
                    );

                    Predicate clientPredicate = criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("client")),
                            pattern
                    );

                    var productJoin = root.join("product");
                    Predicate productNamePredicate = criteriaBuilder.like(
                            criteriaBuilder.lower(productJoin.get("name")),
                            pattern
                    );

                    predicates.add(
                            criteriaBuilder.or(namePredicate, clientPredicate, productNamePredicate)
                    );
                } else if (UserEntity.class.isAssignableFrom(entityClass)) {
                    Predicate namePredicate = criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("name")),
                            pattern
                    );

                    Predicate clientPredicate = criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("email")),
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

