package com.fundicion.lara.repository;

import com.fundicion.lara.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Integer>, JpaSpecificationExecutor<ProductEntity> {
    Optional<ProductEntity> findProductEntitiesByName(String name);

    @Query("""
                SELECT DISTINCT UPPER(p.client)
                FROM ProductEntity p
                WHERE p.client IS NOT NULL
                  AND TRIM(p.client) <> ''
            """)
    List<String> findDistinctClientsUpperCase();

}
