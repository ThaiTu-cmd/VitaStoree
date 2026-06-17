package com.doan.VitaStore.service.impl;

import com.doan.VitaStore.dto.request.admin.CartRequest;
import com.doan.VitaStore.dto.response.admin.CartItemResponse;
import com.doan.VitaStore.dto.response.admin.CartResponse;
import com.doan.VitaStore.entity.CartItemsEntity;
import com.doan.VitaStore.entity.CartsEntity;
import com.doan.VitaStore.entity.ProductsEntity;
import com.doan.VitaStore.entity.UserEntity;
import com.doan.VitaStore.repository.CartItemsRepository;
import com.doan.VitaStore.repository.CartsRepository;
import com.doan.VitaStore.repository.ProductsRepository;
import com.doan.VitaStore.repository.UserRepository;
import com.doan.VitaStore.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartsRepository cartsRepository;

    @Autowired
    private CartItemsRepository cartItemsRepository;

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CartResponse> getAllCarts() {
        return cartsRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCartById(int id) {
        CartsEntity cart = cartsRepository.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));
        return toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addItemToCart(int cartId, CartRequest request) {
        CartsEntity cart = cartsRepository.findById((long) cartId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));

        ProductsEntity product = productsRepository.findById((long) request.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        var existing = cartItemsRepository.findByCartCartIdAndProductProductId(cartId, request.getProductId());
        if (existing.isPresent()) {
            CartItemsEntity item = existing.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemsRepository.save(item);
        } else {
            CartItemsEntity item = new CartItemsEntity();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(request.getQuantity());
            cartItemsRepository.save(item);
        }

        return toResponse(cartsRepository.findById((long) cartId).orElse(cart));
    }

    @Override
    @Transactional
    public CartResponse updateItemQuantity(int cartId, int itemId, int quantity) {
        cartItemsRepository.findById((long) itemId).ifPresent(item -> {
            if (item.getCart().getCartId() == cartId) {
                if (quantity <= 0) {
                    cartItemsRepository.delete(item);
                } else {
                    item.setQuantity(quantity);
                    cartItemsRepository.save(item);
                }
            }
        });

        if (cartItemsRepository.findByCartCartId(cartId).isEmpty()) {
            cartsRepository.deleteById((long) cartId);
            CartResponse empty = new CartResponse();
            empty.setId(cartId);
            empty.setItemCount(0);
            empty.setItems(List.of());
            empty.setTotalAmount(BigDecimal.ZERO);
            return empty;
        }

        CartsEntity cart = cartsRepository.findById((long) cartId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));
        return toResponse(cart);
    }

    @Override
    @Transactional
    public void removeItemFromCart(int cartId, int itemId) {
        cartItemsRepository.findById((long) itemId).ifPresent(item -> {
            if (item.getCart().getCartId() == cartId) {
                cartItemsRepository.delete(item);
                if (cartItemsRepository.findByCartCartId(cartId).isEmpty()) {
                    cartsRepository.delete(item.getCart());
                }
            }
        });
    }

    @Override
    @Transactional
    public void clearCart(int cartId) {
        CartsEntity cart = cartsRepository.findById((long) cartId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));
        cart.getCartItems().clear();
        cartItemsRepository.deleteByCartCartId(cartId);
    }

    @Override
    @Transactional
    public void deleteCart(int id) {
        CartsEntity cart = cartsRepository.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));
        cartItemsRepository.deleteByCartCartId(id);
        cartsRepository.delete(cart);
    }

    @Override
    @Transactional
    public CartResponse getOrCreateCart(int userId) {
        return cartsRepository.findByUserUserId(userId)
                .map(this::toResponse)
                .orElseGet(() -> {
                    UserEntity user = userRepository.findById((long) userId)
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
                    CartsEntity cart = new CartsEntity();
                    cart.setUser(user);
                    cart.setCreatedAt(java.time.LocalDateTime.now());
                    return toResponse(cartsRepository.save(cart));
                });
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Optional<CartResponse> findCartByUserId(int userId) {
        return cartsRepository.findByUserUserId(userId).map(this::toResponse);
    }

    @Override
    @Transactional
    public CartResponse addItemByUser(int userId, CartRequest request) {
        CartsEntity cart = cartsRepository.findByUserUserId(userId)
                .orElseGet(() -> {
                    UserEntity user = userRepository.findById((long) userId)
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
                    CartsEntity newCart = new CartsEntity();
                    newCart.setUser(user);
                    newCart.setCreatedAt(java.time.LocalDateTime.now());
                    return cartsRepository.save(newCart);
                });
        return addItemToCart(cart.getCartId(), request);
    }

    @Override
    @Transactional
    public CartResponse updateItemByUser(int userId, int itemId, int quantity) {
        CartResponse empty = new CartResponse();
        empty.setItemCount(0);
        empty.setItems(List.of());
        empty.setTotalAmount(BigDecimal.ZERO);
        return cartsRepository.findByUserUserId(userId)
                .map(cart -> updateItemQuantity(cart.getCartId(), itemId, quantity))
                .orElse(empty);
    }

    @Override
    @Transactional
    public void removeItemByUser(int userId, int itemId) {
        cartsRepository.findByUserUserId(userId).ifPresent(cart -> {
            removeItemFromCart(cart.getCartId(), itemId);
        });
    }

    @Override
    @Transactional
    public void clearCartByUser(int userId) {
        cartsRepository.findByUserUserId(userId).ifPresent(cart -> {
            cartItemsRepository.deleteByCartCartId(cart.getCartId());
            cartsRepository.delete(cart);
        });
    }

    @Override
    @Transactional
    public CartResponse syncCart(int userId, List<CartRequest> items) {
        CartsEntity cart = cartsRepository.findByUserUserId(userId)
                .orElseGet(() -> {
                    UserEntity user = userRepository.findById((long) userId)
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
                    CartsEntity newCart = new CartsEntity();
                    newCart.setUser(user);
                    newCart.setCreatedAt(java.time.LocalDateTime.now());
                    return cartsRepository.save(newCart);
                });

        cartItemsRepository.deleteByCartCartId(cart.getCartId());

        for (CartRequest req : items) {
            ProductsEntity product = productsRepository.findById((long) req.getProductId())
                    .orElse(null);
            if (product != null && req.getQuantity() > 0) {
                CartItemsEntity item = new CartItemsEntity();
                item.setCart(cart);
                item.setProduct(product);
                item.setQuantity(req.getQuantity());
                cartItemsRepository.save(item);
            }
        }

        return toResponse(cartsRepository.findById((long) cart.getCartId()).orElse(cart));
    }

    private CartResponse toResponse(CartsEntity cart) {
        List<CartItemResponse> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        if (cart.getCartItems() != null) {
            for (CartItemsEntity ci : cart.getCartItems()) {
                BigDecimal lineTotal = ci.getProduct().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));
                total = total.add(lineTotal);
                items.add(new CartItemResponse(
                        ci.getCartItemId(),
                        ci.getProduct().getProductId(),
                        ci.getProduct().getProductName(),
                        ci.getProduct().getPrice(),
                        ci.getProduct().getImageURL(),
                        ci.getQuantity()
                ));
            }
        }

        return new CartResponse(
                cart.getCartId(),
                cart.getUser() != null ? cart.getUser().getUserId() : 0,
                cart.getUser() != null ? cart.getUser().getEmail() : "—",
                cart.getCreatedAt() != null ? cart.getCreatedAt().toString() : null,
                items.size(),
                total,
                items
        );
    }
}
