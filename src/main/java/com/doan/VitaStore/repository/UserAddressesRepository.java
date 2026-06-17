package com.doan.VitaStore.repository;

import com.doan.VitaStore.entity.UserAddressesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAddressesRepository extends JpaRepository<UserAddressesEntity, Long> {
    List<UserAddressesEntity> findByDeletedAtIsNull();
    Optional<UserAddressesEntity> findByAddressIdAndDeletedAtIsNull(int id);
    List<UserAddressesEntity> findByUserUserIdAndDeletedAtIsNull(int userId);
}
