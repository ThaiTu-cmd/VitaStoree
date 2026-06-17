package com.doan.VitaStore.repository;

import com.doan.VitaStore.entity.OrderHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistoryEntity, Long> {
    List<OrderHistoryEntity> findByOrderOrderIdOrderByChangeTimeAsc(int orderId);
}
