package com.doan.VitaStore.dto.response.admin;

public class UserResponse {
    private int id;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private String status;
    private String deletedAt;

    public UserResponse() {
    }

    public UserResponse(int id, String fullName, String email, String phone, String role, String status, String deletedAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = status;
        this.deletedAt = deletedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDeletedAt() { return deletedAt; }
    public void setDeletedAt(String deletedAt) { this.deletedAt = deletedAt; }
}