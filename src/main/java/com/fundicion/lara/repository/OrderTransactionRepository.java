package com.fundicion.lara.repository;

import com.fundicion.lara.entity.OrderTransactionEntity;
import com.fundicion.lara.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderTransactionRepository extends JpaRepository<OrderTransactionEntity, Integer> , JpaSpecificationExecutor<OrderTransactionEntity> {

    @Query("SELECT COUNT(o) FROM OrderTransactionEntity o WHERE o.product = :product")
    long countByProduct(@Param("product") ProductEntity product);

    List<OrderTransactionEntity> findAllByOperationDateBefore(LocalDate date);

}
