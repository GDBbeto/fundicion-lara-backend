package com.fundicion.lara.utils;
import com.fundicion.lara.commons.emuns.TransactionType;
import com.fundicion.lara.dto.request.RequestParams;
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
            predicates.add(criteriaBuilder.between(root.get("operationDate"), req.getStartDate(), req.getEndDate()));

            if (req.getType() != null) {
                predicates.add(root.get("type").in(toCommaListTransactionType(req.getType())));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static List<TransactionType> toCommaListTransactionType(String str) {
        // Divide el String por comas, recorta los espacios y convierte a lista de TransactionType
        List<TransactionType> transactionTypeList = Stream.of(str.split(","))
                .map(String::trim)
                .map(TransactionType::fromString) // Convertir a TransactionType
                .collect(Collectors.toList());
        // Si la lista está vacía, devuelve una lista con un valor por defecto (opcional)
        return transactionTypeList.isEmpty() ? Collections.singletonList(TransactionType.fromString(str)) : transactionTypeList;
    }
}

