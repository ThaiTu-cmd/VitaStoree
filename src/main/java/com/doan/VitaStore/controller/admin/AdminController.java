package com.doan.VitaStore.controller.admin;

import com.doan.VitaStore.dto.request.AdminUserCreationRequest;
import com.doan.VitaStore.dto.request.AdminUserUpdateRequest;
import com.doan.VitaStore.dto.response.AdminUserResponse;
import com.doan.VitaStore.exception.UserNotFoundException;
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

    @GetMapping("/order/list")
    public String orderList() {
        return "admin/order/list";
    }

    @GetMapping("/order/detail")
    public String orderDetail() {
        return "admin/order/detail";
    }

    @GetMapping("/user/all")
    public ResponseEntity<List<AdminUserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/user/add")
    public ResponseEntity<?> addUser(@RequestBody AdminUserCreationRequest request) {
        try {
            AdminUserResponse created = userService.createUserByAdmin(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable int id,
            @RequestBody AdminUserUpdateRequest request) {
        try {
            AdminUserResponse updated = userService.updateUserByAdmin(id, request);
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
            AdminUserResponse restored = userService.restoreUserById(id);
            return ResponseEntity.ok(restored);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
