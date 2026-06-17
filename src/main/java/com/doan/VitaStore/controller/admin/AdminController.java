package com.doan.VitaStore.controller.admin;

import com.doan.VitaStore.dto.request.admin.CategoryRequest;
import com.doan.VitaStore.dto.request.admin.ProductRequest;
import com.doan.VitaStore.dto.request.admin.UserRequest;
import com.doan.VitaStore.dto.request.client.AddressRequest;
import com.doan.VitaStore.dto.request.client.CartRequest;
import com.doan.VitaStore.dto.response.admin.CategoryResponse;
import com.doan.VitaStore.dto.response.admin.ProductResponse;
import com.doan.VitaStore.dto.response.admin.UserResponse;
import com.doan.VitaStore.dto.response.client.AddressResponse;
import com.doan.VitaStore.dto.response.client.CartResponse;
import com.doan.VitaStore.dto.response.admin.CategoryResponse;
import com.doan.VitaStore.dto.response.admin.ProductResponse;
import com.doan.VitaStore.dto.response.admin.UserResponse;
import com.doan.VitaStore.exception.UserNotFoundException;
import com.doan.VitaStore.service.AddressService;
import com.doan.VitaStore.service.CartService;
import com.doan.VitaStore.service.CategoryService;
import com.doan.VitaStore.service.ProductService;
import com.doan.VitaStore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    @Autowired
    private AddressService addressService;

    @GetMapping({"", "/index"})
    public String dashboard() {
        return "admin/index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "admin/Admin_logon";
    }

    @GetMapping("/user/list")
    public String userList() {
        return "admin/user/list";
    }

    @GetMapping("/product/list")
    public String productList() {
        return "admin/product/list";
    }

    @GetMapping("/product/category")
    public String categoryList() {
        return "admin/product/category";
    }

    @GetMapping("/product/add")
    public String productAdd() {
        return "admin/product/add";
    }

    @GetMapping("/cart/list")
    public String cartList() {
        return "admin/cart/list";
    }

    @GetMapping("/order/list")
    public String orderList() {
        return "admin/order/list";
    }

    @GetMapping("/order/detail")
    public String orderDetail() {
        return "admin/order/detail";
    }

    @GetMapping("/user/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/user/add")
    public ResponseEntity<?> addUser(@RequestBody UserRequest request) {
        try {
            UserResponse created = userService.createUserByAdmin(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable int id,
            @RequestBody UserRequest request) {
        try {
            UserResponse updated = userService.updateUserByAdmin(id, request);
            return ResponseEntity.ok(updated);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok(Map.of("message", "Xoá user thành công"));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/user/{id}/restore")
    public ResponseEntity<?> restoreUser(@PathVariable int id) {
        try {
            UserResponse restored = userService.restoreUserById(id);
            return ResponseEntity.ok(restored);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/category/all")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategory());
    }

    @PostMapping("/category/add")
    public  ResponseEntity<?> addCategory(@RequestBody CategoryRequest request) {
        try {
            CategoryResponse created = categoryService.createCategory(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/category/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable int id,
            @RequestBody CategoryRequest request) {
        try {
            CategoryResponse updated = categoryService.updateCategory(id, request);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/category/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable int id) {
        try {
            categoryService.deleteCategoryById((long) id);
            return ResponseEntity.ok(Map.of("message", "Xoá danh mục thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/category/{id}/restore")
    public ResponseEntity<?> restoreCategory(@PathVariable int id) {
        try {
            CategoryResponse restored = categoryService.restoreCategoryById((long) id);
            return ResponseEntity.ok(restored);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ── PRODUCTS ──────────────────────────────────────────────────

    @GetMapping("/product/all")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<?> getProductById(@PathVariable int id) {
        try {
            ProductResponse product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/product/add")
    public ResponseEntity<?> createProduct(@RequestBody ProductRequest request) {
        try {
            ProductResponse created = productService.createProduct(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable int id, @RequestBody ProductRequest request) {
        try {
            ProductResponse updated = productService.updateProduct(id, request);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable int id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(Map.of("message", "Xoá sản phẩm thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/product/{id}/restore")
    public ResponseEntity<?> restoreProduct(@PathVariable int id) {
        try {
            ProductResponse restored = productService.restoreProduct(id);
            return ResponseEntity.ok(restored);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ── CARTS ──────────────────────────────────────────────────

    @GetMapping("/cart/all")
    public ResponseEntity<List<CartResponse>> getAllCarts() {
        return ResponseEntity.ok(cartService.getAllCarts());
    }

    @GetMapping("/cart/{id}")
    public ResponseEntity<?> getCartById(@PathVariable int id) {
        try {
            CartResponse cart = cartService.getCartById(id);
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/cart/{id}/items")
    public ResponseEntity<?> addItemToCart(@PathVariable int id, @RequestBody CartRequest request) {
        try {
            CartResponse updated = cartService.addItemToCart(id, request);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/cart/{cartId}/items/{itemId}")
    public ResponseEntity<?> updateItemQuantity(@PathVariable int cartId, @PathVariable int itemId,
                                                  @RequestBody Map<String, Integer> body) {
        try {
            int quantity = body.getOrDefault("quantity", 1);
            CartResponse updated = cartService.updateItemQuantity(cartId, itemId, quantity);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/cart/{cartId}/items/{itemId}")
    public ResponseEntity<?> removeItemFromCart(@PathVariable int cartId, @PathVariable int itemId) {
        try {
            cartService.removeItemFromCart(cartId, itemId);
            return ResponseEntity.ok(Map.of("message", "Đã xóa mục khỏi giỏ hàng"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/cart/{id}")
    public ResponseEntity<?> deleteCart(@PathVariable int id) {
        try {
            cartService.deleteCart(id);
            return ResponseEntity.ok(Map.of("message", "Đã xóa giỏ hàng"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ===== ADDRESS =====

    @GetMapping("/address/list")
    public String addressList() {
        return "admin/address/list";
    }

    @GetMapping("/address/all")
    public ResponseEntity<List<AddressResponse>> getAllAddresses() {
        return ResponseEntity.ok(addressService.getAllAddresses());
    }

    @GetMapping("/address/{id}")
    public ResponseEntity<?> getAddressById(@PathVariable int id) {
        try {
            return ResponseEntity.ok(addressService.getAddressById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/address/add")
    public ResponseEntity<?> addAddress(@RequestBody AddressRequest request) {
        try {
            AddressResponse created = addressService.createAddress(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/address/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable int id, @RequestBody AddressRequest request) {
        try {
            AddressResponse updated = addressService.updateAddress(id, request);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/address/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable int id) {
        try {
            addressService.deleteAddressById(id);
            return ResponseEntity.ok(Map.of("message", "Đã xóa địa chỉ"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/address/{id}/restore")
    public ResponseEntity<?> restoreAddress(@PathVariable int id) {
        try {
            AddressResponse restored = addressService.restoreAddressById(id);
            return ResponseEntity.ok(restored);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
