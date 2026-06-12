package com.doan.VitaStore.service;

import com.doan.VitaStore.dto.request.UserCreationRequest;
import com.doan.VitaStore.dto.request.UserUpdateRequest;
import com.doan.VitaStore.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();

    UserResponse createUser(UserCreationRequest request);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long id);
}
