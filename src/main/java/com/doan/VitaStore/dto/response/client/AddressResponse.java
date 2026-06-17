package com.doan.VitaStore.dto.response.client;

public class AddressResponse {
    private int id;
    private int userId;
    private String userName;
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String district;
    private String ward;
    private String streetAddress;
    private String fullAddress;
    private boolean isDefault;
    private String createdAt;
    private String deletedAt;

    public AddressResponse() {}

    public AddressResponse(int id, int userId, String userName, String receiverName, String receiverPhone, String province, String district, String ward, String streetAddress, String fullAddress, boolean isDefault, String createdAt, String deletedAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.streetAddress = streetAddress;
        this.fullAddress = fullAddress;
        this.isDefault = isDefault;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public String getReceiverPhone() { return receiverPhone; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getWard() { return ward; }
    public void setWard(String ward) { this.ward = ward; }
    public String getStreetAddress() { return streetAddress; }
    public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }
    public String getFullAddress() { return fullAddress; }
    public void setFullAddress(String fullAddress) { this.fullAddress = fullAddress; }
    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getDeletedAt() { return deletedAt; }
    public void setDeletedAt(String deletedAt) { this.deletedAt = deletedAt; }

    // aliases for client template (address.html)
    public String getFullName() { return receiverName; }
    public void setFullName(String fullName) { this.receiverName = fullName; }
    public String getPhone() { return receiverPhone; }
    public void setPhone(String phone) { this.receiverPhone = phone; }
    public String getType() { return "home"; }
    public void setType(String type) { /* ignored, not stored */ }
}
