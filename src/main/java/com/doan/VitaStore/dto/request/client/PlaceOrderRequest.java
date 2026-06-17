package com.doan.VitaStore.dto.request.client;

public class PlaceOrderRequest {
    private int addressId;
    private String paymentMethod;
    private String note;
    private String cartData;

    public PlaceOrderRequest() {}

    public int getAddressId() { return addressId; }
    public void setAddressId(int addressId) { this.addressId = addressId; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getCartData() { return cartData; }
    public void setCartData(String cartData) { this.cartData = cartData; }
}
