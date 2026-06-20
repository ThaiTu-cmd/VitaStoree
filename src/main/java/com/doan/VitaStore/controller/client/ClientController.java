package com.doan.VitaStore.controller.client;

import com.doan.VitaStore.dto.request.client.AddressRequest;
import com.doan.VitaStore.dto.response.client.ShopCategoryResponse;
import com.doan.VitaStore.dto.response.client.ShopProductResponse;
import com.doan.VitaStore.entity.UserEntity;
import com.doan.VitaStore.security.service.UserDetailsImpl;
import com.doan.VitaStore.dto.response.client.AddressResponse;
import com.doan.VitaStore.dto.response.client.OrderItemResponse;
import com.doan.VitaStore.dto.response.client.OrderResponse;
import com.doan.VitaStore.service.AddressService;
import com.doan.VitaStore.service.OrderService;
import com.doan.VitaStore.service.VNPAYService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import com.doan.VitaStore.service.CategoryService;
import com.doan.VitaStore.service.ProductService;
import com.doan.VitaStore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    private OrderService orderService;

    @Autowired
    private VNPAYService vnpayService;

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

    private String blockAdmin(UserEntity user) {
        if (user != null && user.getRole() == com.doan.VitaStore.enums.Role.ADMIN)
            return "redirect:/";
        return null;
    }

    @GetMapping("/cart")
    public String cart(RedirectAttributes ra) {
        UserEntity user = getCurrentUser();
        if (user != null) { String r = blockAdmin(user); if (r != null) { ra.addFlashAttribute("adminError", "Tính năng này không dành cho Admin"); return r; } }
        return "client/views/cart";
    }

    @GetMapping("/checkout")
    public String checkout(Model model, RedirectAttributes ra) {
        UserEntity user = getCurrentUser();
        if (user == null)
            return "redirect:/auth/login";
        { String r = blockAdmin(user); if (r != null) { ra.addFlashAttribute("adminError", "Tính năng này không dành cho Admin"); return r; } }
        List<AddressResponse> addresses = addressService
                .getAddressesByUser(user.getUserId());
        model.addAttribute("addresses", addresses);
        Map<String, Object> standard = new HashMap<>();
        standard.put("id", 1);
        standard.put("name", "Giao hàng tiêu chuẩn");
        standard.put("fee", BigDecimal.valueOf(30000));
        Map<String, Object> fast = new HashMap<>();
        fast.put("id", 2);
        fast.put("name", "Giao hàng nhanh");
        fast.put("fee", BigDecimal.valueOf(50000));
        model.addAttribute("shippingMethods", List.of(standard, fast));
        Map<String, Integer> fees = new HashMap<>();
        fees.put("1", 30000);
        fees.put("2", 50000);
        try {
            model.addAttribute("shippingFeesJson",
                    new ObjectMapper().writeValueAsString(fees));
        } catch (Exception e) {
            model.addAttribute("shippingFeesJson", "{}");
        }
        return "client/views/checkout";
    }

    @PostMapping("/checkout/place-order")
    public String placeOrder(@RequestParam("cartData") String cartData,
            @RequestParam("addressId") int addressId,
            @RequestParam(value = "paymentMethod", defaultValue = "COD") String paymentMethod,
            @RequestParam(value = "note", required = false) String note,
            HttpServletRequest request,
            RedirectAttributes ra) {
        UserEntity user = getCurrentUser();
        if (user == null)
            return "redirect:/auth/login";
        if (user.getRole() == com.doan.VitaStore.enums.Role.ADMIN) {
            ra.addFlashAttribute("orderError", "Tính năng này không dành cho Admin");
            return "redirect:/checkout";
        }
        try {
            OrderResponse order = orderService.placeOrder(
                    user.getUserId(), addressId, paymentMethod, note, cartData);

            if ("VNPAY".equalsIgnoreCase(paymentMethod)) {
                String ipAddress = request.getRemoteAddr();
                if ("0:0:0:0:0:0:0:1".equals(ipAddress) || "127.0.0.1".equals(ipAddress)) {
                    ipAddress = "127.0.0.1";
                }
                String orderInfo = "Thanh toan don hang " + order.getOrderCode();
                String paymentUrl = vnpayService.createPaymentUrl(
                        order.getTotalAmount().longValue(),
                        orderInfo,
                        String.valueOf(order.getId()),
                        ipAddress
                );
                return "redirect:" + paymentUrl;
            }

            ra.addFlashAttribute("orderSuccess",
                    "Đặt hàng thành công! Mã đơn: " + order.getOrderCode());
        } catch (Exception e) {
            ra.addFlashAttribute("orderError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String orders(Model model, RedirectAttributes ra) {
        UserEntity user = getCurrentUser();
        if (user == null)
            return "redirect:/auth/login";
        { String r = blockAdmin(user); if (r != null) { ra.addFlashAttribute("adminError", "Tính năng này không dành cho Admin"); return r; } }
        model.addAttribute("user", user);
        List<OrderResponse> orders = orderService.getOrdersByUser(user.getUserId());
        model.addAttribute("orders", orders);
        Map<Integer, List<OrderItemResponse>> itemsMap = new HashMap<>();
        Map<Integer, Map<Integer, String>> imagesMap = new HashMap<>();
        for (OrderResponse o : orders) {
            itemsMap.put(o.getId(), o.getItems());
            Map<Integer, String> prodImages = new HashMap<>();
            for (OrderItemResponse item : o.getItems())
                prodImages.put(item.getProductId(), item.getImageUrl());
            imagesMap.put(o.getId(), prodImages);
        }
        model.addAttribute("orderItemsMap", itemsMap);
        model.addAttribute("orderProductImages", imagesMap);
        return "client/views/orders";
    }

    @GetMapping("/orderdetail")
    public String orderDetail(@RequestParam(value = "id", required = false) String idParam,
            Model model, RedirectAttributes ra) {
        UserEntity user = getCurrentUser();
        if (user == null)
            return "redirect:/auth/login";
        { String r = blockAdmin(user); if (r != null) { ra.addFlashAttribute("adminError", "Tính năng này không dành cho Admin"); return r; } }
        if (idParam == null || idParam.isBlank()) {
            ra.addFlashAttribute("orderError", "ID đơn hàng không hợp lệ");
            return "redirect:/orders";
        }
        Integer id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            ra.addFlashAttribute("orderError", "ID đơn hàng không hợp lệ");
            return "redirect:/orders";
        }
        try {
            OrderResponse order = orderService.getOrderDetail(id, user.getUserId());
            model.addAttribute("order", order);
            model.addAttribute("items", order.getItems());
            Map<Integer, String> prodImages = new HashMap<>();
            for (OrderItemResponse item : order.getItems())
                prodImages.put(item.getProductId(), item.getImageUrl());
            model.addAttribute("productImages", prodImages);
            Map<String, Object> shipping = new HashMap<>();
            shipping.put("name", "Giao hàng tiêu chuẩn");
            model.addAttribute("shipping", shipping);
            model.addAttribute("user", user);
        } catch (Exception e) {
            model.addAttribute("orderError", e.getMessage());
        }
        return "client/views/orderdetail";
    }

    @PostMapping("/orders/cancel")
    public String cancelOrder(@RequestParam("id") int orderId,
            RedirectAttributes ra) {
        UserEntity user = getCurrentUser();
        if (user == null)
            return "redirect:/auth/login";
        if (user.getRole() == com.doan.VitaStore.enums.Role.ADMIN) {
            ra.addFlashAttribute("orderError", "Tính năng này không dành cho Admin");
            return "redirect:/orders";
        }
        try {
            orderService.cancelOrder(orderId, user.getUserId());
            ra.addFlashAttribute("orderSuccess", "Đã huỷ đơn hàng thành công");
        } catch (Exception e) {
            ra.addFlashAttribute("orderError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/orders";
    }

    @PostMapping("/orders/confirm-received")
    public String confirmReceived(@RequestParam("id") int orderId,
            RedirectAttributes ra) {
        UserEntity user = getCurrentUser();
        if (user == null)
            return "redirect:/auth/login";
        if (user.getRole() == com.doan.VitaStore.enums.Role.ADMIN) {
            ra.addFlashAttribute("orderError", "Tính năng này không dành cho Admin");
            return "redirect:/orders";
        }
        try {
            orderService.confirmReceived(orderId, user.getUserId());
            ra.addFlashAttribute("orderSuccess", "Xác nhận nhận hàng thành công");
        } catch (Exception e) {
            ra.addFlashAttribute("orderError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/orders";
    }

    @GetMapping("/userprofile")
    public String userProfile(Model model, RedirectAttributes ra) {
        UserEntity user = getCurrentUser();
        if (user == null) return "redirect:/auth/login";
        { String r = blockAdmin(user); if (r != null) { ra.addFlashAttribute("adminError", "Tính năng này không dành cho Admin"); return r; } }
        addUserToModel(model);
        return "client/views/userprofile";
    }

    @GetMapping("/address")
    public String address(Model model, RedirectAttributes ra) {
        UserEntity user = getCurrentUser();
        if (user == null) return "redirect:/auth/login";
        { String r = blockAdmin(user); if (r != null) { ra.addFlashAttribute("adminError", "Tính năng này không dành cho Admin"); return r; } }
        addUserToModel(model);
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
        if (user == null)
            return "redirect:/auth/login";
        if (user.getRole() == com.doan.VitaStore.enums.Role.ADMIN) {
            ra.addFlashAttribute("addressError", "Tính năng này không dành cho Admin");
            return "redirect:/address";
        }
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
        if (user == null)
            return "redirect:/auth/login";
        if (user.getRole() == com.doan.VitaStore.enums.Role.ADMIN) {
            ra.addFlashAttribute("addressError", "Tính năng này không dành cho Admin");
            return "redirect:/address";
        }
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
        UserEntity user = getCurrentUser();
        if (user == null)
            return "redirect:/auth/login";
        if (user.getRole() == com.doan.VitaStore.enums.Role.ADMIN) {
            ra.addFlashAttribute("addressError", "Tính năng này không dành cho Admin");
            return "redirect:/address";
        }
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
        UserEntity user = getCurrentUser();
        if (user == null)
            return "redirect:/auth/login";
        if (user.getRole() == com.doan.VitaStore.enums.Role.ADMIN) {
            ra.addFlashAttribute("addressError", "Tính năng này không dành cho Admin");
            return "redirect:/address";
        }
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
        if (user == null)
            return "redirect:/auth/login";
        if (user.getRole() == com.doan.VitaStore.enums.Role.ADMIN) {
            ra.addFlashAttribute("profileError", "Tính năng này không dành cho Admin");
            return "redirect:/userprofile";
        }
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
        if (user == null)
            return "redirect:/auth/login";
        if (user.getRole() == com.doan.VitaStore.enums.Role.ADMIN) {
            ra.addFlashAttribute("passwordError", "Tính năng này không dành cho Admin");
            return "redirect:/userprofile";
        }
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
        if (user != null)
            model.addAttribute("user", user);
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

    @GetMapping({ "/auth", "/auth/" })
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

    @PostMapping("/auth/forgot-password")
    public String handleForgotPassword(@RequestParam("email") String email,
            RedirectAttributes ra) {
        try {
            userService.forgotPassword(email);
            ra.addFlashAttribute("success", "Mã OTP đã được gửi đến email của bạn.");
            return "redirect:/auth/verify-otp?email=" + email;
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/forgot-password";
        }
    }

    @PostMapping("/auth/verify-otp")
    public String handleVerifyOtp(@RequestParam("email") String email,
            @RequestParam("otp") String otp,
            RedirectAttributes ra) {
        try {
            String token = userService.verifyOtp(email, otp);
            return "redirect:/auth/reset-password?email=" + email + "&token=" + token;
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/verify-otp?email=" + email;
        }
    }

    @PostMapping("/auth/reset-password")
    public String handleResetPassword(@RequestParam("email") String email,
            @RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes ra) {
        try {
            if (!password.equals(confirmPassword)) {
                throw new IllegalArgumentException("Mật khẩu xác nhận không khớp.");
            }
            if (password.length() < 8) {
                throw new IllegalArgumentException("Mật khẩu phải có ít nhất 8 ký tự.");
            }
            userService.resetPassword(email, token, password);
            ra.addFlashAttribute("success", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/reset-password?email=" + email + "&token=" + token;
        }
    }

    @PostMapping("/auth/register")
    public String handleRegister(
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
            @RequestParam(value = "agreeTerms", required = false) String agreeTerms,
            RedirectAttributes redirectAttributes) {

        addRegisterFormAttributes(redirectAttributes, firstName, lastName, email, phone);

        String normalizedFirstName = normalize(firstName);
        String normalizedLastName = normalize(lastName);
        String normalizedEmail = normalize(email);
        String normalizedPhone = normalize(phone);
        String normalizedPassword = normalize(password);
        String normalizedConfirmPassword = normalize(confirmPassword);

        if (normalizedFirstName.isBlank()
                || normalizedLastName.isBlank()
                || normalizedEmail.isBlank()
                || normalizedPhone.isBlank()
                || normalizedPassword.isBlank()
                || normalizedConfirmPassword.isBlank()) {
            return registerError(redirectAttributes, "Vui lòng nhập đầy đủ thông tin đăng ký.");
        }

        if (!normalizedEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return registerError(redirectAttributes, "Email không hợp lệ.");
        }

        if (normalizedPassword.length() < 8) {
            return registerError(redirectAttributes, "Mật khẩu phải có ít nhất 8 ký tự.");
        }

        if (!normalizedPassword.equals(normalizedConfirmPassword)) {
            return registerError(redirectAttributes, "Mật khẩu xác nhận không khớp.");
        }

        if (!"on".equalsIgnoreCase(normalize(agreeTerms))) {
            return registerError(redirectAttributes, "Vui lòng đồng ý điều khoản dịch vụ và chính sách bảo mật.");
        }

        if (userService.existsByEmail(normalizedEmail)) {
            return registerError(redirectAttributes, "Email đã được đăng ký.");
        }

        String fullName = normalizedFirstName + " " + normalizedLastName;
        userService.registerUser(fullName, normalizedEmail, normalizedPhone, normalizedPassword);

        redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Chào mừng bạn đến với VitaStore.");
        return "redirect:/auth/login";
    }

    private void addRegisterFormAttributes(RedirectAttributes redirectAttributes,
            String firstName, String lastName, String email, String phone) {
        redirectAttributes.addAttribute("firstName", normalize(firstName));
        redirectAttributes.addAttribute("lastName", normalize(lastName));
        redirectAttributes.addAttribute("email", normalize(email));
        redirectAttributes.addAttribute("phone", normalize(phone));
    }

    private String registerError(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addAttribute("error", message);
        return "redirect:/auth/register";
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
