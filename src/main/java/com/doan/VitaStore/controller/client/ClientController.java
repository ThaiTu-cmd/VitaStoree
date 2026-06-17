package com.doan.VitaStore.controller.client;

import com.doan.VitaStore.dto.response.client.ShopCategoryResponse;
import com.doan.VitaStore.dto.response.client.ShopProductResponse;
import com.doan.VitaStore.entity.UserEntity;
import java.util.List;
import com.doan.VitaStore.service.CategoryService;
import com.doan.VitaStore.service.ProductService;
import com.doan.VitaStore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ClientController {
    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/")
    public String home() {
        return "client/views/index";
    }

    @GetMapping("/shop")
    public String shop(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "price", required = false) String price,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model) {
        Integer categoryId = null;
        if (category != null && !category.isBlank()) {
            List<ShopCategoryResponse> allCats = categoryService.getShopCategories();
            for (ShopCategoryResponse c : allCats) {
                if (c.getSlug().equals(category)) {
                    categoryId = c.getId();
                    break;
                }
            }
        }
        Page<ShopProductResponse> productPage = productService.getShopProducts(q, categoryId, sort, price, page);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("currentPage", page);

        List<ShopCategoryResponse> categories = categoryService.getShopCategories();
        model.addAttribute("categories", categories);

        model.addAttribute("countUnder500k", productService.countUnderPrice(500000));
        model.addAttribute("count500kTo1500k", productService.countByPriceRange(500000, 1500000));
        model.addAttribute("count1500kTo3000k", productService.countByPriceRange(1500000, 3000000));
        model.addAttribute("countOver3000k", productService.countOverPrice(3000000));

        return "client/views/shop";
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
