package com.doan.VitaStore.repository;

import com.doan.VitaStore.entity.UserAddressesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAddressesRepository extends JpaRepository<UserAddressesEntity, Long> {

}
