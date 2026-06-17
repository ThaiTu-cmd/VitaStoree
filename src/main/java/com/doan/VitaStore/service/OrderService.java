package com.doan.VitaStore.service;

import com.doan.VitaStore.dto.response.admin.AdminOrderDetailResponse;
import com.doan.VitaStore.dto.response.admin.AdminOrderResponse;
import com.doan.VitaStore.dto.response.client.OrderResponse;
import java.util.List;

public interface OrderService {
    OrderResponse placeOrder(int userId, int addressId, String paymentMethod,
                             String note, String cartDataJson);
    List<OrderResponse> getOrdersByUser(int userId);
    OrderResponse getOrderDetail(int orderId, int userId);
    void cancelOrder(int orderId, int userId);
    void confirmReceived(int orderId, int userId);
    List<AdminOrderResponse> getAllOrders();
    AdminOrderDetailResponse getAdminOrderDetail(int orderId);
    AdminOrderResponse updateOrderStatus(int orderId, String newStatus);
    void deleteOrder(int orderId);
}
