package com.doan.VitaStore.repository;

import com.doan.VitaStore.entity.PaymentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentsRepository extends JpaRepository<PaymentsEntity, Long> {
}
