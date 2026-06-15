package com.doan.VitaStore.repository;

import com.doan.VitaStore.entity.CartsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartsRepository extends JpaRepository<CartsEntity, Long> {
}
