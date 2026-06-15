package com.doan.VitaStore.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClientController {

    @GetMapping("/")
    public String home() {
        return "client/views/index";
    }

    @GetMapping("/shop")
    public String shop() {
        return "client/views/shop";
    }

    @GetMapping("/detail")
    public String detail() {
        return "client/views/detail";
    }

    @GetMapping("/cart")
    public String cart() {
        return "client/views/cart";
    }

    @GetMapping("/checkout")
    public String checkout() {
        return "client/views/checkout";
    }

    @GetMapping("/orders")
    public String orders() {
        return "client/views/orders";
    }

    @GetMapping("/orderdetail")
    public String orderDetail() {
        return "client/views/orderdetail";
    }

    @GetMapping("/userprofile")
    public String userProfile() {
        return "client/views/userprofile";
    }

    @GetMapping("/address")
    public String address() {
        return "client/views/address";
    }

    @GetMapping("/about")
    public String about() {
        return "client/views/about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "client/views/contact";
    }

    @GetMapping("/blog")
    public String blog() {
        return "client/views/shop";
    }

    @GetMapping("/auth/logout")
    public String clientLogout() {
        return "redirect:/";
    }

    @GetMapping("/auth/login")
    public String clientLogin() {
        return "client/auth/login";
    }

    @GetMapping("/auth/register")
    public String clientRegister() {
        return "client/auth/register";
    }

    @GetMapping("/auth/forgot-password")
    public String forgotPassword() {
        return "client/auth/forgot-password";
    }

    @GetMapping("/auth/reset-password")
    public String resetPassword() {
        return "client/auth/reset-password";
    }

    @GetMapping("/auth/verify-otp")
    public String verifyOtp() {
        return "client/auth/verify-otp";
    }
}
