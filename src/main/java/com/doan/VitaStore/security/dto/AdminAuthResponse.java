package com.doan.VitaStore.security.dto;

public class AdminAuthResponse {
    private final String username;

    public AdminAuthResponse(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
