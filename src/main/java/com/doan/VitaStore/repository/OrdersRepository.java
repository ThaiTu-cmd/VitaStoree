package com.doan.VitaStore.repository;

import com.doan.VitaStore.entity.OrdersEntity;
import com.doan.VitaStore.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends JpaRepository<OrdersEntity, Long> {
    long countByStatus(OrderStatus status);
}
