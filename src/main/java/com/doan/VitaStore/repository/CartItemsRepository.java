package com.doan.VitaStore.repository;

import com.doan.VitaStore.entity.CartItemsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemsRepository extends JpaRepository<CartItemsEntity, Long> {
}
