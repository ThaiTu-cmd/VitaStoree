package com.doan.VitaStore.repository;

import com.doan.VitaStore.entity.OrdersEntity;
import com.doan.VitaStore.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<OrdersEntity, Long> {
    long countByStatus(OrderStatus status);
    List<OrdersEntity> findByUserUserIdOrderByOrderDateDesc(int userId);
    Optional<OrdersEntity> findByOrderId(int orderId);

    @Query(value = "SELECT DATE(o.OrderDate) AS day, SUM(o.TotalAmount) " +
                   "FROM Orders o " +
                   "WHERE o.Status = 'COMPLETED' AND o.OrderDate BETWEEN :start AND :end " +
                   "GROUP BY DATE(o.OrderDate) " +
                   "ORDER BY day ASC", nativeQuery = true)
    List<Object[]> getDailyRevenue(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}