package com.doan.VitaStore.repository;

import com.doan.VitaStore.entity.OrdersEntity;
import com.doan.VitaStore.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<OrdersEntity, Long> {
    long countByStatus(OrderStatus status);
    List<OrdersEntity> findByUserUserIdOrderByOrderDateDesc(int userId);
    Optional<OrdersEntity> findByOrderId(int orderId);
}