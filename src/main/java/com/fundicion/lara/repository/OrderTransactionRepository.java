package com.fundicion.lara.repository;

import com.fundicion.lara.entity.OrderTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderTransactionRepository extends JpaRepository<OrderTransactionEntity, Integer> , JpaSpecificationExecutor<OrderTransactionEntity> {
}
