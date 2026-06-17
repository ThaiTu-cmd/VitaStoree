package com.doan.VitaStore.service;

import com.doan.VitaStore.dto.request.client.CartRequest;
import com.doan.VitaStore.dto.response.client.CartResponse;

import java.util.List;

public interface CartService {
    List<CartResponse> getAllCarts();
    CartResponse getCartById(int id);
    CartResponse addItemToCart(int cartId, CartRequest request);
    CartResponse updateItemQuantity(int cartId, int itemId, int quantity);
    void removeItemFromCart(int cartId, int itemId);
    void clearCart(int cartId);
    void deleteCart(int id);

    CartResponse getOrCreateCart(int userId);
    java.util.Optional<CartResponse> findCartByUserId(int userId);
    CartResponse addItemByUser(int userId, CartRequest request);
    CartResponse updateItemByUser(int userId, int itemId, int quantity);
    void removeItemByUser(int userId, int itemId);
    void clearCartByUser(int userId);
    CartResponse syncCart(int userId, List<CartRequest> items);
}
