package com.fundicion.lara.repository;

import com.fundicion.lara.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> , JpaSpecificationExecutor<TransactionEntity> {
}
