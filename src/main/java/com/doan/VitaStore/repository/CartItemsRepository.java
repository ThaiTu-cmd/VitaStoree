package com.doan.VitaStore.repository;

import com.doan.VitaStore.entity.CartItemsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemsRepository extends JpaRepository<CartItemsEntity, Long> {
    List<CartItemsEntity> findByCartCartId(int cartId);
    Optional<CartItemsEntity> findByCartCartIdAndProductProductId(int cartId, int productId);
    void deleteByCartCartId(int cartId);
}
