package com.doan.VitaStore.controller.client;

import com.doan.VitaStore.dto.request.client.AddressRequest;
import com.doan.VitaStore.dto.response.client.ShopCategoryResponse;
import com.doan.VitaStore.dto.response.client.ShopProductResponse;
import com.doan.VitaStore.entity.UserEntity;
import com.doan.VitaStore.security.service.UserDetailsImpl;
import com.doan.VitaStore.service.AddressService;
import java.util.Collections;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class ClientController {
    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AddressService addressService;

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
    public String orders(Model model) {
        addUserToModel(model);
        model.addAttribute("orders", Collections.emptyList());
        model.addAttribute("orderItemsMap", new HashMap<>());
        model.addAttribute("orderProductImages", new HashMap<>());
        return "client/views/orders";
    }

    @GetMapping("/orderdetail")
    public String orderDetail() {
        return "client/views/orderdetail";
    }

    @GetMapping("/userprofile")
    public String userProfile(Model model) {
        addUserToModel(model);
        return "client/views/userprofile";
    }

    @GetMapping("/address")
    public String address(Model model) {
        addUserToModel(model);
        UserEntity user = getCurrentUser();
        if (user != null) {
            model.addAttribute("addresses", addressService.getAddressesByUser(user.getUserId()));
        } else {
            model.addAttribute("addresses", Collections.emptyList());
        }
        return "client/views/address";
    }

    @PostMapping("/address/add")
    public String addAddress(
            @RequestParam("fullName") String fullName,
            @RequestParam("phone") String phone,
            @RequestParam("city") String city,
            @RequestParam("province") String province,
            @RequestParam("addressLine2") String addressLine2,
            @RequestParam("addressLine1") String addressLine1,
            @RequestParam(value = "isDefault", required = false) String isDefault,
            RedirectAttributes ra) {
        UserEntity user = getCurrentUser();
        if (user == null) return "redirect:/auth/login";
        try {
            AddressRequest req = new AddressRequest(
                    user.getUserId(), fullName, phone, city, province,
                    addressLine2, addressLine1, "true".equals(isDefault));
            addressService.createAddress(req);
            ra.addFlashAttribute("addressSuccess", "Thêm địa chỉ thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("addressError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/address";
    }

    @PostMapping("/address/update")
    public String updateAddress(
            @RequestParam("id") int id,
            @RequestParam("fullName") String fullName,
            @RequestParam("phone") String phone,
            @RequestParam("city") String city,
            @RequestParam("province") String province,
            @RequestParam("addressLine2") String addressLine2,
            @RequestParam("addressLine1") String addressLine1,
            @RequestParam(value = "isDefault", required = false) String isDefault,
            RedirectAttributes ra) {
        UserEntity user = getCurrentUser();
        if (user == null) return "redirect:/auth/login";
        try {
            AddressRequest req = new AddressRequest(
                    user.getUserId(), fullName, phone, city, province,
                    addressLine2, addressLine1, "true".equals(isDefault));
            addressService.updateAddress(id, req);
            ra.addFlashAttribute("addressSuccess", "Cập nhật địa chỉ thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("addressError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/address";
    }

    @PostMapping("/address/delete")
    public String deleteAddress(@RequestParam("id") int id, RedirectAttributes ra) {
        try {
            addressService.deleteAddressById(id);
            ra.addFlashAttribute("addressSuccess", "Đã xóa địa chỉ");
        } catch (Exception e) {
            ra.addFlashAttribute("addressError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/address";
    }

    @PostMapping("/address/set-default")
    public String setDefaultAddress(@RequestParam("id") int id, RedirectAttributes ra) {
        try {
            addressService.setDefaultById(id);
            ra.addFlashAttribute("addressSuccess", "Đã đặt làm mặc định");
        } catch (Exception e) {
            ra.addFlashAttribute("addressError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/address";
    }

    @PostMapping("/userprofile/update")
    public String updateProfile(
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam(value = "phone", required = false) String phone,
            RedirectAttributes ra) {
        UserEntity user = getCurrentUser();
        if (user == null) return "redirect:/auth/login";
        try {
            if (!email.equals(user.getEmail()) && userService.existsByEmail(email)) {
                throw new IllegalArgumentException("Email đã được sử dụng");
            }
            userService.updateProfile(user.getUserId(), fullName, phone);
            ra.addFlashAttribute("profileSuccess", "Cập nhật thông tin thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("profileError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/userprofile";
    }

    @PostMapping("/userprofile/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes ra) {
        UserEntity user = getCurrentUser();
        if (user == null) return "redirect:/auth/login";
        try {
            if (!newPassword.equals(confirmPassword)) {
                throw new IllegalArgumentException("Mật khẩu xác nhận không khớp");
            }
            userService.changePassword(user.getUserId(), currentPassword, newPassword);
            ra.addFlashAttribute("passwordSuccess", "Đổi mật khẩu thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("passwordError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/userprofile";
    }

    @PostMapping("/api/user/verify-password")
    @ResponseBody
    public Map<String, Boolean> verifyPassword(@RequestParam("password") String password) {
        UserEntity user = getCurrentUser();
        boolean valid = user != null && userService.verifyPassword(user.getUserId(), password);
        return Map.of("valid", valid);
    }

    private void addUserToModel(Model model) {
        UserEntity user = getCurrentUser();
        if (user != null) model.addAttribute("user", user);
    }

    private UserEntity getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) auth.getPrincipal()).getUser();
        }
        return null;
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

    @GetMapping({"/auth", "/auth/"})
    public String authRedirect() {
        return "redirect:/auth/login";
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

        redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Chào mừng bạn đến với VitaStore.");
        return "redirect:/auth/login";
    }
}
