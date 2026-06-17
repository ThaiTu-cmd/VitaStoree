package com.doan.VitaStore.repository;

import com.doan.VitaStore.entity.CategoriesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoriesRepository extends JpaRepository<CategoriesEntity, Long> {
    List<CategoriesEntity> findByDeletedAtIsNull();
}
