package com.fundicion.lara.repository;

import com.fundicion.lara.commons.emuns.TransactionType;
import com.fundicion.lara.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> , JpaSpecificationExecutor<TransactionEntity> {
     Optional<TransactionEntity> findByOrderTransactionId(Integer orderTransactionId);

     @Query("SELECT SUM(t.amount) FROM TransactionEntity t WHERE t.type = :type AND t.operationDate BETWEEN :startDate AND :endDate")
     BigDecimal getTotalByTypeAndDateRange(
             @Param("type") TransactionType type,
             @Param("startDate") LocalDate startDate,
             @Param("endDate") LocalDate endDate
     );
}
