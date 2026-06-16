package com.doan.VitaStore.controller.client;

import com.doan.VitaStore.entity.UserEntity;
import com.doan.VitaStore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ClientController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

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

    @PostMapping("/auth/register")
    public String handleRegister(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addAttribute("error", "Mật khẩu xác nhận không khớp.");
            return "redirect:/auth/register";
        }

        if (userService.existsByEmail(email)) {
            redirectAttributes.addAttribute("error", "Email đã được đăng ký.");
            return "redirect:/auth/register";
        }

        String fullName = (firstName + " " + lastName).trim();
        UserEntity user = userService.registerUser(fullName, email, phone, password);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        redirectAttributes.addAttribute("success", "Đăng ký thành công! Chào mừng bạn đến với VitaStore.");
        return "redirect:/auth/login";
    }
}
