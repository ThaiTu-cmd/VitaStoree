package com.doan.VitaStore.service.impl;

import com.doan.VitaStore.dto.request.client.CartItemJson;
import com.doan.VitaStore.dto.response.admin.AdminOrderDetailResponse;
import com.doan.VitaStore.dto.response.admin.AdminOrderResponse;
import com.doan.VitaStore.dto.response.client.OrderItemResponse;
import com.doan.VitaStore.dto.response.client.OrderResponse;
import com.doan.VitaStore.entity.*;
import com.doan.VitaStore.enums.OrderStatus;
import com.doan.VitaStore.enums.PaymentMethod;
import com.doan.VitaStore.enums.PaymentStatus;
import com.doan.VitaStore.repository.*;
import com.doan.VitaStore.service.OrderService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired private OrdersRepository ordersRepository;
    @Autowired private OrderItemsRepository orderItemsRepository;
    @Autowired private PaymentsRepository paymentsRepository;
    @Autowired private OrderHistoryRepository orderHistoryRepository;
    @Autowired private ProductsRepository productsRepository;
    @Autowired private UserAddressesRepository addressRepository;
    @Autowired private CartItemsRepository cartItemsRepository;
    @Autowired private CartsRepository cartsRepository;
    @Autowired private UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public OrderResponse placeOrder(int userId, int addressId,
            String paymentMethod, String note, String cartDataJson) {

        UserEntity user = userRepository.findById((long) userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        UserAddressesEntity addr = addressRepository
                .findByAddressIdAndDeletedAtIsNull(addressId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        List<CartItemJson> cartItems;
        try {
            cartItems = objectMapper.readValue(cartDataJson,
                    new TypeReference<List<CartItemJson>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Dữ liệu giỏ hàng không hợp lệ");
        }
        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItemJson ci : cartItems) {
            totalAmount = totalAmount.add(
                BigDecimal.valueOf(ci.getPrice()).multiply(BigDecimal.valueOf(ci.getQty())));
        }

        OrdersEntity order = new OrdersEntity();
        order.setUser(user);
        order.setCustomerName(user.getFullName());
        order.setCustomerPhone(user.getPhone());
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);
        order.setReceiverName(addr.getReceiverName());
        order.setReceiverPhone(addr.getReceiverPhone());
        order.setProvince(addr.getProvince());
        order.setDistrict(addr.getDistrict());
        order.setWard(addr.getWard());
        order.setStreetAddress(addr.getStreetAddress());
        order = ordersRepository.save(order);

        for (CartItemJson ci : cartItems) {
            ProductsEntity product = productsRepository.findById((long) ci.getId()).orElse(null);
            OrderItemsEntity item = new OrderItemsEntity();
            item.setOrder(order);
            item.setProduct(product);
            item.setProductName(ci.getName());
            item.setPrice(BigDecimal.valueOf(ci.getPrice()));
            item.setQuantity(ci.getQty());
            orderItemsRepository.save(item);
        }

        PaymentMethod pm = PaymentMethod.COD;
        try {
            if (paymentMethod != null && !paymentMethod.isBlank()) {
                pm = PaymentMethod.valueOf(paymentMethod.toUpperCase());
            }
        } catch (Exception ignored) {}

        PaymentsEntity payment = new PaymentsEntity();
        payment.setOrder(order);
        payment.setPaymentMethod(pm);
        payment.setAmount(totalAmount);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        paymentsRepository.save(payment);

        OrderHistoryEntity history = new OrderHistoryEntity();
        history.setOrder(order);
        history.setStatus(OrderStatus.PENDING);
        history.setNote(note != null && !note.isBlank() ? note : "Đơn hàng được tạo");
        history.setChangeTime(LocalDateTime.now());
        orderHistoryRepository.save(history);

        cartsRepository.findByUserUserId(userId).ifPresent(cart -> {
            cartItemsRepository.deleteByCartCartId(cart.getCartId());
            cartsRepository.delete(cart);
        });

        return toOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser(int userId) {
        return ordersRepository.findByUserUserIdOrderByOrderDateDesc(userId)
                .stream().map(this::toOrderResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderDetail(int orderId, int userId) {
        OrdersEntity order = ordersRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        if (order.getUser().getUserId() != userId)
            throw new RuntimeException("Không có quyền xem đơn hàng này");
        return toOrderResponse(order);
    }

    @Override
    @Transactional
    public void cancelOrder(int orderId, int userId) {
        OrdersEntity order = ordersRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        if (order.getUser().getUserId() != userId)
            throw new RuntimeException("Không có quyền huỷ đơn hàng này");
        if (order.getStatus() == OrderStatus.COMPLETED
                || order.getStatus() == OrderStatus.CANCELLED
                || order.getStatus() == OrderStatus.SHIPPING)
            throw new RuntimeException("Không thể huỷ đơn hàng ở trạng thái hiện tại");
        order.setStatus(OrderStatus.CANCELLED);
        ordersRepository.save(order);
        OrderHistoryEntity h = new OrderHistoryEntity();
        h.setOrder(order); h.setStatus(OrderStatus.CANCELLED);
        h.setNote("Khách hàng đã huỷ đơn");
        h.setChangeTime(LocalDateTime.now());
        orderHistoryRepository.save(h);
    }

    @Override
    @Transactional
    public void confirmReceived(int orderId, int userId) {
        OrdersEntity order = ordersRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        if (order.getUser().getUserId() != userId)
            throw new RuntimeException("Không có quyền xác nhận đơn hàng này");
        if (order.getStatus() != OrderStatus.SHIPPING
                && order.getStatus() != OrderStatus.CONFIRMED)
            throw new RuntimeException("Không thể xác nhận đã nhận hàng");
        order.setStatus(OrderStatus.COMPLETED);
        ordersRepository.save(order);
        OrderHistoryEntity h = new OrderHistoryEntity();
        h.setOrder(order); h.setStatus(OrderStatus.COMPLETED);
        h.setNote("Khách hàng đã xác nhận nhận hàng");
        h.setChangeTime(LocalDateTime.now());
        orderHistoryRepository.save(h);
    }

    // --- ADMIN ---------------------------------------------

    @Override @Transactional(readOnly = true)
    public List<AdminOrderResponse> getAllOrders() {
        return ordersRepository.findAll().stream()
                .map(this::toAdminResponse).toList();
    }

    @Override @Transactional(readOnly = true)
    public AdminOrderDetailResponse getAdminOrderDetail(int orderId) {
        OrdersEntity order = ordersRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        AdminOrderDetailResponse dto = new AdminOrderDetailResponse();
        dto.setId(order.getOrderId());
        dto.setOrderCode("#DH" + order.getOrderId());
        dto.setUserId(order.getUser().getUserId());
        dto.setUsername(order.getUser().getFullName());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus().name().toLowerCase());
        dto.setCreatedAt(order.getOrderDate() != null ? order.getOrderDate().toString() : "");
        dto.setReceiverName(order.getReceiverName());
        dto.setReceiverPhone(order.getReceiverPhone());
        dto.setProvince(order.getProvince());
        dto.setDistrict(order.getDistrict());
        dto.setWard(order.getWard());
        dto.setStreetAddress(order.getStreetAddress());
        if (order.getPayment() != null) {
            dto.setPaymentMethod(order.getPayment().getPaymentMethod().name());
            dto.setPaymentStatus(order.getPayment().getPaymentStatus().name());
        }
        if (order.getOrderHistories() != null && !order.getOrderHistories().isEmpty())
            dto.setNote(order.getOrderHistories().get(0).getNote());
        List<Map<String, Object>> itemList = new ArrayList<>();
        if (order.getOrderItems() != null) {
            for (OrderItemsEntity oi : order.getOrderItems()) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("productId", oi.getProduct() != null ? oi.getProduct().getProductId() : 0);
                m.put("productName", oi.getProductName());
                m.put("price", oi.getPrice());
                m.put("quantity", oi.getQuantity());
                m.put("lineTotal", oi.getPrice().multiply(BigDecimal.valueOf(oi.getQuantity())));
                m.put("imageUrl", oi.getProduct() != null ? oi.getProduct().getImageURL() : "");
                itemList.add(m);
            }
        }
        dto.setItems(itemList);
        List<Map<String, Object>> historyList = new ArrayList<>();
        if (order.getOrderHistories() != null) {
            for (OrderHistoryEntity h : order.getOrderHistories()) {
                Map<String, Object> hm = new LinkedHashMap<>();
                hm.put("status", h.getStatus().name());
                hm.put("note", h.getNote());
                hm.put("changeTime", h.getChangeTime() != null ? h.getChangeTime().toString() : "");
                historyList.add(hm);
            }
        }
        dto.setHistory(historyList);
        return dto;
    }

    @Override @Transactional
    public AdminOrderResponse updateOrderStatus(int orderId, String newStatus) {
        OrdersEntity order = ordersRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        OrderStatus status;
        try { status = OrderStatus.valueOf(newStatus.toUpperCase()); }
        catch (Exception e) { throw new RuntimeException("Trạng thái không hợp lệ: " + newStatus); }
        OrderStatus current = order.getStatus();
        if (current == OrderStatus.COMPLETED || current == OrderStatus.CANCELLED)
            throw new RuntimeException("Không thể thay đổi trạng thái đơn hàng đã " + (current == OrderStatus.COMPLETED ? "hoàn thành" : "hủy"));
        if (status == OrderStatus.PENDING && current != OrderStatus.PENDING)
            throw new RuntimeException("Không thể chuyển về trạng thái chờ xử lý");
        if (status == OrderStatus.CONFIRMED && current != OrderStatus.PENDING)
            throw new RuntimeException("Không thể chuyển về trạng thái đã xác nhận từ trạng thái hiện tại");
        if (status == OrderStatus.SHIPPING && current != OrderStatus.CONFIRMED && current != OrderStatus.PENDING)
            throw new RuntimeException("Không thể chuyển sang trạng thái đang giao từ trạng thái hiện tại");
        order.setStatus(status);
        ordersRepository.save(order);
        OrderHistoryEntity h = new OrderHistoryEntity();
        h.setOrder(order); h.setStatus(status);
        h.setNote("Admin cập nhật trạng thái -> " + status.name());
        h.setChangeTime(LocalDateTime.now());
        orderHistoryRepository.save(h);
        return toAdminResponse(order);
    }

    @Override @Transactional
    public void deleteOrder(int orderId) {
        OrdersEntity order = ordersRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        ordersRepository.delete(order);
    }

    private OrderResponse toOrderResponse(OrdersEntity order) {
        List<OrderItemResponse> itemResponses = new ArrayList<>();
        if (order.getOrderItems() != null) {
            for (OrderItemsEntity oi : order.getOrderItems()) {
                BigDecimal lineTotal = oi.getPrice().multiply(BigDecimal.valueOf(oi.getQuantity()));
                String imgUrl = (oi.getProduct() != null && oi.getProduct().getImageURL() != null)
                        ? oi.getProduct().getImageURL() : "";
                itemResponses.add(new OrderItemResponse(
                        oi.getProduct() != null ? oi.getProduct().getProductId() : 0,
                        oi.getProductName(), oi.getPrice(), oi.getQuantity(),
                        lineTotal, imgUrl));
            }
        }
        String pm = "COD";
        PaymentStatus ps = PaymentStatus.PENDING;
        if (order.getPayment() != null) {
            pm = order.getPayment().getPaymentMethod().name();
            ps = order.getPayment().getPaymentStatus();
        }
        String note = "";
        if (order.getOrderHistories() != null && !order.getOrderHistories().isEmpty())
            note = order.getOrderHistories().get(0).getNote();
        return new OrderResponse(order.getOrderId(), order.getOrderDate(),
                order.getStatus(), order.getTotalAmount(),
                order.getReceiverName(), order.getReceiverPhone(),
                order.getProvince(), order.getDistrict(), order.getWard(),
                order.getStreetAddress(), pm, ps, note, itemResponses);
    }

    private AdminOrderResponse toAdminResponse(OrdersEntity order) {
        return new AdminOrderResponse(order.getOrderId(), "#DH" + order.getOrderId(),
                order.getUser() != null ? order.getUser().getFullName() : "",
                order.getUser() != null ? order.getUser().getUserId() : 0,
                order.getTotalAmount(),
                order.getStatus().name().toLowerCase(),
                order.getOrderDate() != null ? order.getOrderDate().toString() : "");
    }
}
