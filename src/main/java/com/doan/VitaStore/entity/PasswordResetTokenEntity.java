package com.doan.VitaStore.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PasswordResetTokens")
public class PasswordResetTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 6)
    private String otp;

    @Column(nullable = false, length = 255)
    private String token;

    @Column(nullable = false)
    private LocalDateTime otpExpiry;

    @Column(nullable = false)
    private LocalDateTime tokenExpiry;

    @Column(nullable = false)
    private boolean used = false;

    public PasswordResetTokenEntity() {}

    public PasswordResetTokenEntity(String email, String otp, String token, LocalDateTime otpExpiry, LocalDateTime tokenExpiry) {
        this.email = email;
        this.otp = otp;
        this.token = token;
        this.otpExpiry = otpExpiry;
        this.tokenExpiry = tokenExpiry;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public LocalDateTime getOtpExpiry() { return otpExpiry; }
    public void setOtpExpiry(LocalDateTime otpExpiry) { this.otpExpiry = otpExpiry; }
    public LocalDateTime getTokenExpiry() { return tokenExpiry; }
    public void setTokenExpiry(LocalDateTime tokenExpiry) { this.tokenExpiry = tokenExpiry; }
    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
}
