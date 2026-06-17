package com.doan.VitaStore.dto.request.client;

public class OrderRequest {
    private int  orderId;
    private String paymentMethod;
    private String note;
    private String cartData;

    public OrderRequest() {
    }

    public OrderRequest(int orderId, String paymentMethod, String note, String cartData) {
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.note = note;
        this.cartData = cartData;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCartData() {
        return cartData;
    }

    public void setCartData(String cartData) {
        this.cartData = cartData;
    }
}
