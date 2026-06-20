package com.doan.VitaStore.service;

import com.doan.VitaStore.dto.request.admin.UserRequest;
import com.doan.VitaStore.dto.response.admin.UserResponse;
import com.doan.VitaStore.entity.UserEntity;

import java.util.List;

public interface UserService {
    UserEntity registerUser(String fullName, String email, String phone, String password);

    UserEntity getUserByEmail(String email);

    boolean existsByEmail(String email);

    List<UserResponse> getAllUsers();

    UserResponse getUserByIdResponse(int id);

    UserResponse createUserByAdmin(UserRequest request);

    UserResponse updateUserByAdmin(int id, UserRequest request);

    void deleteUserById(int id);
    UserResponse restoreUserById(int id);

    UserResponse updateProfile(int userId, String fullName, String phone);
    UserResponse changePassword(int userId, String currentPassword, String newPassword);
    boolean verifyPassword(int userId, String rawPassword);

    void forgotPassword(String email);
    String verifyOtp(String email, String otp);
    void resetPassword(String email, String token, String newPassword);
}