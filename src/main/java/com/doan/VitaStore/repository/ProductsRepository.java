package com.doan.VitaStore.repository;

import com.doan.VitaStore.entity.ProductsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

@Repository
public interface ProductsRepository extends JpaRepository<ProductsEntity, Long>, JpaSpecificationExecutor<ProductsEntity> {
    long countByDeletedAtIsNullAndPriceBetween(BigDecimal min, BigDecimal max);
    long countByDeletedAtIsNullAndPriceLessThan(BigDecimal max);
    long countByDeletedAtIsNullAndPriceGreaterThan(BigDecimal min);
}
