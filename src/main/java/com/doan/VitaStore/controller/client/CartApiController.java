package com.doan.VitaStore.controller.client;

import com.doan.VitaStore.dto.request.client.CartRequest;
import com.doan.VitaStore.dto.response.client.CartResponse;
import com.doan.VitaStore.enums.Role;
import com.doan.VitaStore.security.service.UserDetailsImpl;
import com.doan.VitaStore.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {

    @Autowired
    private CartService cartService;

    private boolean isAdmin(UserDetailsImpl principal) {
        return principal != null && principal.getUser().getRole() == Role.ADMIN;
    }

    @GetMapping
    public ResponseEntity<?> getCart(@AuthenticationPrincipal UserDetailsImpl principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Chưa đăng nhập"));
        }
        return cartService.findCartByUserId(principal.getUser().getUserId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.ok(new CartResponse()));
    }

    @PostMapping("/sync")
    public ResponseEntity<?> syncCart(@AuthenticationPrincipal UserDetailsImpl principal,
                                       @RequestBody List<CartRequest> items) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Chưa đăng nhập"));
        }
        if (isAdmin(principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Tính năng này không dành cho Admin", "redirect", "/"));
        }
        CartResponse cart = cartService.syncCart(principal.getUser().getUserId(), items);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    public ResponseEntity<?> addItem(@AuthenticationPrincipal UserDetailsImpl principal,
                                      @RequestBody CartRequest request) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Chưa đăng nhập"));
        }
        if (isAdmin(principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Tính năng này không dành cho Admin", "redirect", "/"));
        }
        CartResponse cart = cartService.addItemByUser(principal.getUser().getUserId(), request);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<?> updateItem(@AuthenticationPrincipal UserDetailsImpl principal,
                                         @PathVariable int itemId,
                                         @RequestBody Map<String, Integer> body) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Chưa đăng nhập"));
        }
        if (isAdmin(principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Tính năng này không dành cho Admin", "redirect", "/"));
        }
        int quantity = body.getOrDefault("quantity", 1);
        CartResponse cart = cartService.updateItemByUser(principal.getUser().getUserId(), itemId, quantity);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<?> removeItem(@AuthenticationPrincipal UserDetailsImpl principal,
                                         @PathVariable int itemId) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Chưa đăng nhập"));
        }
        if (isAdmin(principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Tính năng này không dành cho Admin", "redirect", "/"));
        }
        cartService.removeItemByUser(principal.getUser().getUserId(), itemId);
        return ResponseEntity.ok(Map.of("message", "Đã xóa mục khỏi giỏ hàng"));
    }

    @DeleteMapping
    public ResponseEntity<?> clearCart(@AuthenticationPrincipal UserDetailsImpl principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Chưa đăng nhập"));
        }
        if (isAdmin(principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Tính năng này không dành cho Admin", "redirect", "/"));
        }
        cartService.clearCartByUser(principal.getUser().getUserId());
        return ResponseEntity.ok(Map.of("message", "Đã xóa giỏ hàng"));
    }
}
