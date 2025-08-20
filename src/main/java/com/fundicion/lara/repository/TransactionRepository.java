package com.fundicion.lara.repository;

import com.fundicion.lara.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> , JpaSpecificationExecutor<TransactionEntity> {
     Optional<TransactionEntity> findByOrderTransactionId(Integer orderTransactionId);
}
