package com.doan.VitaStore.repository;

import com.doan.VitaStore.entity.CartsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CartsRepository extends JpaRepository<CartsEntity, Long> {
    Optional<CartsEntity> findByUserUserId(int userId);
}
