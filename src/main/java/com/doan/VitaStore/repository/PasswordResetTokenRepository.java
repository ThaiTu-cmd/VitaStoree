package com.doan.VitaStore.repository;

import com.doan.VitaStore.entity.PasswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, Integer> {
    Optional<PasswordResetTokenEntity> findByEmailAndOtpAndUsedFalse(String email, String otp);
    Optional<PasswordResetTokenEntity> findByEmailAndTokenAndUsedFalse(String email, String token);
    Optional<PasswordResetTokenEntity> findTopByEmailOrderByOtpExpiryDesc(String email);
}
