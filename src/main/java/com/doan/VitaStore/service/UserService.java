package com.doan.VitaStore.service;

import com.doan.VitaStore.dto.request.AdminUserCreationRequest;
import com.doan.VitaStore.dto.request.AdminUserUpdateRequest;
import com.doan.VitaStore.dto.response.AdminUserResponse;
import com.doan.VitaStore.entity.UserEntity;

import java.util.List;

public interface UserService {
    UserEntity registerUser(String fullName, String email, String phone, String password);

    UserEntity getUserByEmail(String email);

    boolean existsByEmail(String email);

    List<AdminUserResponse> getAllUsers();

    AdminUserResponse getUserByIdResponse(int id);

    AdminUserResponse createUserByAdmin(AdminUserCreationRequest adminUserCreationRequest);

    AdminUserResponse updateUserByAdmin(int id, AdminUserUpdateRequest adminUserUpdateRequest);

    void deleteUserById(int id);
    AdminUserResponse restoreUserById(int id);
}